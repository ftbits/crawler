package dev.filiptanu.crawler;

import static dev.filiptanu.crawler.ResultProcessor.POISON_PILL;

import java.io.IOException;
import java.util.HashSet;
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

    private Set<String> toCrawl;
    private Set<String> crawled;
    private Set<String> resultUrls;

    private Source source;
    private BlockingQueue<String> resultUrlsQueue;
    private ResultProcessor resultProcessor;
    private CrawlingStrategy crawlingStrategy;

    private Crawler() {
        toCrawl = new HashSet<>();
        crawled = new HashSet<>();
        resultUrls = new HashSet<>();
    }

    public Crawler(Source source, BlockingQueue<String> resultUrlsQueue, ResultProcessor resultProcessor, CrawlingStrategy crawlingStrategy) {
        this();

        this.source = source;
        this.resultUrlsQueue = resultUrlsQueue;
        this.resultProcessor = resultProcessor;
        this.crawlingStrategy = crawlingStrategy;

        if (source.getSeed() != null) {
            toCrawl.add(source.getSeed());
        }
    }

    @Override
    public void run() {
        resultProcessor.start();

        while (!toCrawl.isEmpty()) {
            String url = toCrawl.iterator().next();

            logger.info("Crawling: " + url);

            if (!crawled.contains(url)) {
                CrawlingStrategyResponse crawlingStrategyResponse = crawlingStrategy.crawl(url);

                crawlingStrategyResponse.getUrlsToFollow().forEach(urlToFollow -> {
                    if (!crawled.contains(urlToFollow)) {
                        toCrawl.add(urlToFollow);
                    }
                });

                crawlingStrategyResponse.getResultUrls().forEach(resultUrl -> {
                    if (!resultUrls.contains(resultUrl)) {
                        try {
                            resultUrlsQueue.put(resultUrl);
                        } catch (InterruptedException e) {
                            logger.warning(e.getMessage());
                        }
                    }
                });

                toCrawl.remove(url);
                crawled.add(url);
            }
        }

        resultUrlsQueue.add(POISON_PILL.name());

        logger.info("Finished crawling " + source.getName());
    }

}
