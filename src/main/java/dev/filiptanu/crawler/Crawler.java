package dev.filiptanu.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

public class Crawler extends Thread {

    private Source source;
    private Set<String> toCrawl;
    private Set<String> crawled;
    private Set<String> resultUrls;
    private BlockingQueue<String> resultUrlsQueue;
    private boolean finished;
    private Semaphore semaphore;
    private Thread resultProcessor;

    public Crawler(Source source, ResultRepository resultRepository) {
        this.source = source;
        toCrawl = new HashSet<String>();
        crawled = new HashSet<String>();
        resultUrls = new HashSet<String>();
        resultUrlsQueue = new LinkedBlockingDeque<String>();
        finished = false;
        semaphore = new Semaphore(0);
        resultProcessor = new ResultProcessor(this, resultRepository, semaphore);

        toCrawl.add(source.getSeed());
    }

    public void run() {
        resultProcessor.start();

        while (!toCrawl.isEmpty()) {
            String url = toCrawl.iterator().next();

            System.out.println("Crawling: " + url);

            if (!crawled.contains(url)) {
                try {
                    Document document = Jsoup.connect(url).get();

                    for (String cssQuery : source.getToFollowUrlCssQueries()) {
                        Elements toFollow = document.select(cssQuery);

                        for (Element element : toFollow) {
                            String urlToFollow = element.attr("href");

                            if (!crawled.contains(urlToFollow)) {
                                toCrawl.add(urlToFollow);
                            }
                        }
                    }

                    for (String cssQuery : source.getResultPageCssQueries()) {
                        Elements results = document.select(cssQuery);

                        for (Element element : results) {
                            String resultUrl = element.attr("href");

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
                    System.err.println("Malformed URL: " + url);
                } catch (IOException e) {
                    e.printStackTrace();
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
        System.out.println("Finished crawling " + source.getName());
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

}