package torrent.manager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"torrent.manager.model"})
@ComponentScan(basePackages = {"torrent.manager.controller", "torrent.manager.deamon"})
@EnableJpaRepositories(basePackages = {"torrent.manager.dao"})
@EnableConfigurationProperties(Config.class)
public class TorrentManager {

	public static void main(String[] args) {
		System.out.println("julia");
		SpringApplication.run(TorrentManager.class, args);
	}

}
