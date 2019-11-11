package rocks.filip.crawler;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class StandardCrawlingStrategy implements CrawlingStrategy {

    private static final Logger logger = Logger.getLogger(StandardCrawlingStrategy.class.getName());

    private Source source;
    private DocumentRetriever documentRetriever;
    private Map<String, String> cookies;

    public StandardCrawlingStrategy(Source source, DocumentRetriever documentRetriever, Map<String, String> cookies) {
        this.source = source;
        this.documentRetriever = documentRetriever;
        this.cookies = cookies;

        Optional<Connection.Response> connectionOptional = ConnectionFactory.getResponse(source.getSeed(), null);
        if (connectionOptional.isPresent()) {
            Connection.Response response = connectionOptional.get();
            cookies.putAll(response.cookies());
        }
    }

    @Override
    public CrawlingStrategyResponse crawl(String url) {
        CrawlingStrategyResponse crawlingStrategyResponse = new CrawlingStrategyResponse();

        try {
            Optional<Document> documentOptional = documentRetriever.retrieveDocumentFromUrl(url, cookies);
            if (documentOptional.isPresent()) {
                Document document = documentOptional.get();

                for (String cssQuery : source.getToFollowUrlCssQueries()) {
                    Elements toFollow = document.select(cssQuery);

                    for (Element element : toFollow) {
                        String urlToFollow = element.attr("href");

                        if (source.getUrlCleanupStrategy().isPresent()) {
                            urlToFollow = source.getUrlCleanupStrategy().get().apply(urlToFollow);
                        }
                        if (!crawlingStrategyResponse.getUrlsToFollow().contains(urlToFollow)) {
                            crawlingStrategyResponse.addUrlToFollow(urlToFollow);
                        }
                    }
                }

                for (String cssQuery : source.getResultPageCssQueries()) {
                    Elements results = document.select(cssQuery);

                    for (Element element : results) {
                        String resultUrl = element.attr("href");

                        if (source.getUrlCleanupStrategy().isPresent()) {
                            resultUrl = source.getUrlCleanupStrategy().get().apply(resultUrl);
                        }

                        if (!crawlingStrategyResponse.getResultUrls().contains(resultUrl)) {
                            crawlingStrategyResponse.addResultUrl(resultUrl);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Malformed URL: " + url);
        } catch (IOException e) {
            logger.warning(e.toString());
        }

        return crawlingStrategyResponse;
    }

}
