package torrent.manager.torrentservice;

public class Download {
    public String url;
    public String pid;

    public Download() {
        this.url = "";
        this.pid = "";
    }
    
    public Download(String url, String pid) {
        this.url = url;
        this.pid = pid;
    }
}