package torrent.manager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"torrent.manager.model"})
@ComponentScan(basePackages = {"torrent.manager.controller"})
@EnableJpaRepositories(basePackages = {"torrent.manager.dao"})
public class TorrentManager {

	public static void main(String[] args) {
		SpringApplication.run(TorrentManager.class, args);
	}

}
