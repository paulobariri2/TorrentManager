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
        for (String p : path) {
            url.append("/").append(p);
        }
        System.out.println("\n\n###################\n\n" + url.toString() + "\n\n###################\n\n");
        return url.toString();
    }
}