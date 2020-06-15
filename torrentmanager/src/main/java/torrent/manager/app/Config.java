package torrent.manager.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

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