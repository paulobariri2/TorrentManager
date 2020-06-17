package torrent.manager.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
public class Config {
    private String deamonSleepTime;
    private String torrentServiceHost;
    private String torrentServicePort;

    public String getDeamonSleepTime() {
        return deamonSleepTime;
    }

    public String getTorrentServicePort() {
        return torrentServicePort;
    }

    public void setTorrentServicePort(String torrentServicePort) {
        this.torrentServicePort = torrentServicePort;
    }

    public String getTorrentServiceHost() {
        return torrentServiceHost;
    }

    public void setTorrentServiceHost(String torrentServiceHost) {
        this.torrentServiceHost = torrentServiceHost;
    }

    public void setDeamonSleepTime(String deamonSleepTime) {
        this.deamonSleepTime = deamonSleepTime;
    }
}