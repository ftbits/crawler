package dev.filiptanu.crawler;

import static dev.filiptanu.crawler.ResultProcessor.POISON_PILL;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

    private static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private Source source;
    private Set<String> toCrawl;
    private Set<String> crawled;
    private Set<String> resultUrls;
    private BlockingQueue<String> resultUrlsQueue;
    private ResultProcessorWorker resultProcessorWorker;
    private Map<String, String> cookies;
    private DocumentRetriever documentRetriever;

    private Crawler() {
        toCrawl = new HashSet<>();
        crawled = new HashSet<>();
        resultUrls = new HashSet<>();
    }

    public Crawler(Source source, BlockingQueue<String> resultUrlsQueue, ResultProcessorWorker resultProcessorWorker, Map<String, String> cookies, DocumentRetriever documentRetriever) {
        this();

        this.source = source;
        this.resultUrlsQueue = resultUrlsQueue;
        this.resultProcessorWorker = resultProcessorWorker;
        this.cookies = cookies;
        this.documentRetriever = documentRetriever;

        toCrawl.add(source.getSeed());

        Optional<Connection.Response> connectionOptional = ConnectionFactory.getResponse(source.getSeed(), null);
        if (connectionOptional.isPresent()) {
            Response response = connectionOptional.get();
            cookies.putAll(response.cookies());
        }
    }

    public void run() {
        resultProcessorWorker.start();

        while (!toCrawl.isEmpty()) {
            String url = toCrawl.iterator().next();

            logger.info("Crawling: " + url);

            if (!crawled.contains(url)) {
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
                                if (!crawled.contains(urlToFollow)) {
                                    toCrawl.add(urlToFollow);
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

                                if (!resultUrls.contains(resultUrl)) {
                                    resultUrls.add(resultUrl);

                                    try {
                                        resultUrlsQueue.put(resultUrl);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    logger.warning("Malformed URL: " + url);
                } catch (IOException e) {
                    logger.warning(e.toString());
                }

                toCrawl.remove(url);
                crawled.add(url);
            }
        }

        resultUrlsQueue.add(POISON_PILL);

        logger.info("Finished crawling " + source.getName());
    }

}