package torrent.manager.error;

public class ErrorCode {
    private ErrorStatusTP status;
    private String message;

    public ErrorCode() {
        this.status = ErrorStatusTP.SUCCESS;
    }

    public ErrorCode(ErrorStatusTP status) {
        this.status = status;
    }

    public ErrorCode(ErrorStatusTP status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorStatusTP getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(ErrorStatusTP status) {
        this.status = status;
    }

}