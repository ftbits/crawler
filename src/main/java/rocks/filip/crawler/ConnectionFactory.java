package rocks.filip.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

public class ConnectionFactory {

    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());

    private static List<String> proxies;

    static {
        proxies = new ArrayList<>();

        try {
            initializeProxyList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeProxyList() throws IOException {
        InputStream is = ConnectionFactory.class.getResourceAsStream("/proxy-list.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            proxies.add(line);
        }
    }

    public static Response getResponse(String url, Map<String, String> cookies, boolean useProxy) {
        while (true) {
            try {
                Connection connection = getConnection(url, cookies, useProxy);
                return connection.execute();
            } catch (ConnectException e) {
                logger.warning("Connection refused for url: " + url);
            } catch (SocketTimeoutException e) {
                logger.warning("Timeout for url: " + url);
            } catch (HttpStatusException e) {
                logger.warning("Got status " + e.getStatusCode() + " for url " + url);
            } catch (NoRouteToHostException e) {
                logger.warning("Cannot connect to proxy for url " + url);
            } catch (SocketException e) {
                logger.warning("Connection reset for url " + url);
            } catch (IOException e) {
                logger.warning("IOException: " + e.getMessage());
            }
        }
    }

    private static Connection getConnection(String url, Map<String, String> cookies, boolean useProxy) {
        Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .referrer("http://www.google.com")
                .followRedirects(true);

        if (useProxy) {
            String proxy = getRandomProxy();
            logger.info("Using proxy: " + proxy);

            String[] proxyParts = proxy.split(":");

            connection.proxy(proxyParts[0], Integer.parseInt(proxyParts[1]));
        }

        if (cookies != null) {
            connection.cookies(cookies);
        }

        return connection;
    }

    private static String getRandomProxy() {
        return proxies.get(ThreadLocalRandom.current().nextInt(0, proxies.size()));
    }

}