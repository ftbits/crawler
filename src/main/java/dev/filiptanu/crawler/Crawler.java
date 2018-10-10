package dev.filiptanu.crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

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
    private boolean finished;
    private Semaphore semaphore;
    private Thread resultProcessor;
    private Map<String, String> cookies;

    public Crawler(Source source, ResultRepository resultRepository) {
        this.source = source;
        toCrawl = new HashSet<>();
        crawled = new HashSet<>();
        resultUrls = new HashSet<>();
        resultUrlsQueue = new LinkedBlockingDeque<>();
        finished = false;
        semaphore = new Semaphore(0);
        resultProcessor = new ResultProcessor(this, resultRepository, semaphore);

        toCrawl.add(source.getSeed());

        cookies = ConnectionFactory.getResponse(source.getSeed(), null, source.isUseProxy()).cookies();
    }

    public void run() {
        resultProcessor.start();

        while (!toCrawl.isEmpty()) {
            String url = toCrawl.iterator().next();

            logger.info("Crawling: " + url);

            if (!crawled.contains(url)) {
                try {
                    Document document = ConnectionFactory.getResponse(url, cookies, source.isUseProxy()).parse();

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
                } catch (IllegalArgumentException e) {
                    logger.warning("Malformed URL: " + url);
                } catch (IOException e) {
                    logger.warning(e.toString());
                }

                toCrawl.remove(url);
                crawled.add(url);
            }
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finished = true;
        logger.info("Finished crawling " + source.getName());
    }

    public Source getSource() {
        return source;
    }

    public BlockingQueue<String> getResultUrlsQueue() {
        return resultUrlsQueue;
    }

    public boolean isFinished() {
        return finished;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

}