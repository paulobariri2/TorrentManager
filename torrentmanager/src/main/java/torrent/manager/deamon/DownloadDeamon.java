package torrent.manager.deamon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import torrent.manager.app.Config;
import torrent.manager.dao.TorrentRepository;
import torrent.manager.model.Torrent;
import torrent.manager.model.TorrentStatusTP;

@Component
public class DownloadDeamon implements ApplicationRunner {

    private Torrent currentTorrent;

    @Autowired
    private TorrentRepository torrentRepository;

    @Autowired
    private Config config;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        currentTorrent = null;
        while (true) {
            try {
                Iterable<Torrent> torrents = torrentRepository.findAll();
                Torrent startedTorrent = findStartedTorrent(torrents);
                if (startedTorrent != null) {
                    currentTorrent = startedTorrent;
                } else {
                    currentTorrent = findNextTorrent(torrents);
                }

                System.out
                        .println("Current torrent is " + (currentTorrent == null ? "NULL" : currentTorrent.toString()));
                Thread.sleep(Long.parseLong(config.getDeamonSleepTime()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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