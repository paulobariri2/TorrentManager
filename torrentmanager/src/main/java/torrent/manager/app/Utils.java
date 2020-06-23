package torrent.manager.app;

public class Utils {

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.isEmpty());
    }

    public static String buildUrlString(String host, String port, String... path) {
        StringBuilder url = new StringBuilder("https://");
        url.append(host);
        if (!Utils.isNullOrEmpty(port)) {
            url.append(":").append(port);
        }
        if (path != null) {
            for (String p : path) {
                url.append("/").append(p);
            }
        }
        return url.toString();
    }
}