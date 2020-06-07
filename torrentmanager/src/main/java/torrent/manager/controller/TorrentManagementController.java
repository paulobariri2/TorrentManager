package torrent.manager.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import torrent.manager.dao.TorrentRepository;
import torrent.manager.model.Torrent;
import torrent.manager.model.TorrentStatusTP;

@Controller
@RequestMapping(path = "/torrent")
public class TorrentManagementController {

    @Autowired
    private TorrentRepository torrentRepository;

    @PostMapping(path = "/download")
    public ResponseEntity<Torrent> download(@RequestBody Torrent torrent) {
        try {
            torrent.setPid("111");
            Timestamp nowDateTime = new Timestamp(System.currentTimeMillis());
            torrent.setDateTime(nowDateTime);
            torrent.setStatus(TorrentStatusTP.READY);
            torrentRepository.save(torrent);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Torrent>(torrent, HttpStatus.CREATED);
    }
}