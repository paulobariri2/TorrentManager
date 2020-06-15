package torrent.manager.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config")
public class Config {
    private String deamonSleepTime;

    public String getDeamonSleepTime() {
        return deamonSleepTime;
    }

    public void setDeamonSleepTime(String deamonSleepTime) {
        this.deamonSleepTime = deamonSleepTime;
    }
}