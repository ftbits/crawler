package rocks.filip.crawler;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());

    public static Optional<Response> getResponse(String url, Map<String, String> cookies) {
        Connection connection = getConnection(url, cookies);

        try {
            return Optional.ofNullable(connection.execute());
        } catch (IOException e) {
            logger.log(Level.INFO, e.getMessage());
        }

        return Optional.empty();
    }

    private static Connection getConnection(String url, Map<String, String> cookies) {
        Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .referrer("http://www.google.com")
                .followRedirects(true)
                .parser(Parser.xmlParser());

        if (cookies != null) {
            connection.cookies(cookies);
        }

        return connection;
    }

}