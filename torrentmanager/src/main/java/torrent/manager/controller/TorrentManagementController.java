package torrent.manager.controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping(path = "/update")
    public ResponseEntity<Torrent> updateTorrent(@RequestBody Torrent torrent) {
        long id = torrent.getId();
        Optional<Torrent> optional = torrentRepository.findById(id);
        if (optional.isPresent()) {
            torrentRepository.save(torrent);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Torrent>(torrent, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Torrent> getTorrent(@PathVariable long id) {
        Optional<Torrent> optional = torrentRepository.findById(id);
        if (!optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Torrent torrent = optional.get();
        return new ResponseEntity<Torrent>(torrent, HttpStatus.OK);
    }
}