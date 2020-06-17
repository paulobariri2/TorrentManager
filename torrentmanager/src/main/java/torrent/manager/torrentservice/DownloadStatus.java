package torrent.manager.torrentservice;

public class DownloadStatus {
    public String pid;
    public String status;
    
    public DownloadStatus(String pid, String status) {
        this.pid = pid;
        this.status = status;
    }
}