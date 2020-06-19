package torrent.manager.deamon;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import torrent.manager.app.Config;
import torrent.manager.app.Utils;
import torrent.manager.dao.TorrentRepository;
import torrent.manager.error.ErrorCode;
import torrent.manager.error.ErrorStatusTP;
import torrent.manager.model.Torrent;
import torrent.manager.model.TorrentStatusTP;
import torrent.manager.torrentservice.Download;
import torrent.manager.torrentservice.DownloadStatus;

@Component
public class DownloadDeamon implements ApplicationRunner {

    private static final Object RUNNING = "Running";

    private Torrent currentTorrent;

    @Autowired
    private TorrentRepository torrentRepository;

    @Autowired
    private Config config;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        currentTorrent = null;
        while (true) {
            ErrorCode errorCode = new ErrorCode();
            try {
                Iterable<Torrent> torrents = torrentRepository.findAll();
                Torrent startedTorrent = findStartedTorrent(torrents);
                if (startedTorrent != null) {
                    currentTorrent = startedTorrent;
                } else {
                    currentTorrent = findNextTorrent(torrents);
                }

                boolean isTorrentDownloading = isTorrentDownloading(currentTorrent, errorCode);
                if (ErrorStatusTP.ERROR.equals(errorCode.getStatus())) {
                    System.err.println(errorCode.getMessage());
                    Thread.sleep(Long.parseLong(config.getDeamonSleepTime()));
                    continue;
                }

                if (!isTorrentDownloading) {
                    errorCode = startDownload(currentTorrent);
                    if (ErrorStatusTP.ERROR.equals(errorCode.getStatus())) {
                        System.err.println(errorCode.getMessage());
                        Thread.sleep(Long.parseLong(config.getDeamonSleepTime()));
                        continue;
                    }
                }

                System.out
                        .println("Current torrent is " + (currentTorrent == null ? "NULL" : currentTorrent.toString()));
                Thread.sleep(Long.parseLong(config.getDeamonSleepTime()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ErrorCode startDownload(Torrent torrent) {
        ResponseEntity<String> result = callPostDownloadFromTorrentService(torrent);
        ErrorCode errorCode = validateResponse(result);
        if (ErrorStatusTP.ERROR.equals(errorCode.getStatus())) {
            return errorCode;
        }

        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            Download downloadInfo = jsonMapper.readValue(result.getBody(), Download.class);
            System.out.println("Starting " + downloadInfo.url + " download with pid=" + downloadInfo.pid);
            torrent.setPid(downloadInfo.pid);
            torrent.setStatus(TorrentStatusTP.STARTED);
            torrentRepository.save(torrent);
        } catch (JsonProcessingException e) {
            return new ErrorCode(ErrorStatusTP.ERROR, "Error to convert json into Download class.\n" + e.getMessage());
        }

        return errorCode;
    }

    private ResponseEntity<String> callPostDownloadFromTorrentService(Torrent torrent) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("{\"url\":\"" + torrent.getUrl() + "\"}", headers);
        String url = Utils.buildUrlString(config.getTorrentServiceHost(), config.getTorrentServicePort(), "download");
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return result;
    }

    private ErrorCode validateResponse(ResponseEntity<String> response) {
        ErrorCode errorCode = new ErrorCode();
        if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
            errorCode.setStatus(ErrorStatusTP.ERROR);
            errorCode.setMessage(response.getBody());
            return errorCode;
        }

        String body = response.getBody();
        if (Utils.isNullOrEmpty(body)) {
            errorCode.setStatus(ErrorStatusTP.ERROR);
            errorCode.setMessage("Response body is empty.");
            return errorCode;
        }
        return errorCode;
    }

    private boolean isTorrentDownloading(Torrent torrent, ErrorCode errorCode) {
        String pid = torrent.getPid();
        if (Utils.isNullOrEmpty(pid)) {
            return false;
        }

        ResponseEntity<String> result = null;
        try {
            result = callGetDownloadStatusFromTorrentService(pid);
        } catch (Exception e) {
            errorCode.setStatus(ErrorStatusTP.ERROR);
            errorCode.setMessage("Error calling TorrentService Download Status.\n" + e.getMessage());
            return false;
        }
        if (isResponseValid(result, errorCode)) {
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                DownloadStatus downloadStatus = jsonMapper.readValue(result.getBody(), DownloadStatus.class);
                if (pid.equals(downloadStatus.pid) && RUNNING.equals(downloadStatus.status)) {
                    return true;
                }
            } catch (JsonProcessingException e) {
                errorCode.setStatus(ErrorStatusTP.ERROR);
                errorCode.setMessage("Error to convert json into DownloadStatus class.\n" + e.getMessage());
                return false;
            }
        }

        return false;
    }

    private ResponseEntity<String> callGetDownloadStatusFromTorrentService(String pid) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        String url = Utils.buildUrlString(config.getTorrentServiceHost(), config.getTorrentServicePort(), "download",
                pid);
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return result;
    }

    private boolean isResponseValid(ResponseEntity<String> result, ErrorCode errorCode) {
        if (result == null) {
            errorCode.setStatus(ErrorStatusTP.ERROR);
            errorCode.setMessage("Response is null.");
            return false;
        }
        HttpStatus statusCode = result.getStatusCode();
        if (HttpStatus.OK.equals(statusCode)) {
            String body = result.getBody();
            if (Utils.isNullOrEmpty(body)) {
                errorCode.setStatus(ErrorStatusTP.ERROR);
                errorCode.setMessage("Call to Torrent Service`s Download Status returned empty body.");
                return false;
            }
        } else {
            errorCode.setStatus(ErrorStatusTP.ERROR);
            errorCode.setMessage(result.getBody());
            return false;
        }
        return true;
    }

    private Torrent findNextTorrent(Iterable<Torrent> torrents) {
        Torrent nextTorrent = null;
        Torrent readyTorrent = findFirstReadyTorrent(torrents);
        if (readyTorrent != null) {
            nextTorrent = readyTorrent;
        }

        return nextTorrent;
    }

    private Torrent findFirstReadyTorrent(Iterable<Torrent> torrents) {
        Torrent readyTorrent = null;
        for (Torrent torrent : torrents) {
            TorrentStatusTP status = torrent.getStatus();
            if (TorrentStatusTP.READY.equals(status)) {
                readyTorrent = torrent;
                break;
            }
        }
        return readyTorrent;
    }

    private Torrent findStartedTorrent(Iterable<Torrent> torrents) {
        Torrent startedTorrent = null;
        for (Torrent torrent : torrents) {
            TorrentStatusTP status = torrent.getStatus();
            if (TorrentStatusTP.STARTED.equals(status)) {
                startedTorrent = torrent;
                break;
            }
        }
        return startedTorrent;
    }

}