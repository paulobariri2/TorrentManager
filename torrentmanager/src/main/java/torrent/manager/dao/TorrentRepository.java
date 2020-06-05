package torrent.manager.dao;

import org.springframework.data.repository.CrudRepository;
import torrent.manager.model.Torrent;

public interface TorrentRepository extends CrudRepository<Torrent, Long> {
    
}