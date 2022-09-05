package mk.filip.crawler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static mk.filip.crawler.Constants.POISON_PILL;

@Getter
@Setter
@ToString
public class Crawler extends Thread {

    public static class Builder {

        private Set<String> toCrawl;
        private Set<String> crawled;
        private Set<String> results;

        private Source source;
        private CrawlingStrategy crawlingStrategy;
        private ResultProcessingStrategy resultProcessingStrategy;
        private ResultRepository resultRepository;

        public Builder(Source source, ResultRepository resultRepository) {
            if (source == null) {
                throw new IllegalArgumentException("The source must be provided.");
            }
            if (resultRepository == null) {
                throw new IllegalArgumentException("The result repository must be provided.");
            }

            toCrawl = new HashSet<>();
            crawled = new HashSet<>();
            results = new HashSet<>();

            this.source = source;
            this.resultRepository = resultRepository;
        }

        public Builder toCrawl(Set<String> toCrawl) {
            this.toCrawl.addAll(toCrawl);

            return this;
        }

        public Builder crawled(Set<String> crawled) {
            this.crawled.addAll(crawled);

            return this;
        }

        public Builder results(Set<String> results) {
            this.results.addAll(results);

            return this;
        }

        public Builder crawlingStrategy(CrawlingStrategy crawlingStrategy) {
            this.crawlingStrategy = crawlingStrategy;

            return this;
        }

        public Builder resultProcessingStrategy(ResultProcessingStrategy resultProcessingStrategy) {
            this.resultProcessingStrategy = resultProcessingStrategy;

            return this;
        }

        public Crawler build() {
            BlockingQueue<String> resultUrlsQueue = new LinkedBlockingQueue<>();

            DocumentRetriever documentRetriever = new DocumentRetriever();
            Map<String, String> cookies = new HashMap<>();
            if (resultProcessingStrategy == null) {
                resultProcessingStrategy = new StandardResultProcessingStrategy(source, documentRetriever, cookies, resultRepository);
            }

            ResultProcessor resultProcessor = new ResultProcessor(resultUrlsQueue, resultProcessingStrategy);
            if (crawlingStrategy == null) {
                crawlingStrategy = new StandardCrawlingStrategy(source, documentRetriever, cookies);
            }

            Crawler crawler = new Crawler(source, crawlingStrategy, resultUrlsQueue, resultProcessor);
            if ("" != source.getSeed()) {
                toCrawl.add(source.getSeed());
            }
            crawler.setToCrawl(toCrawl);
            crawler.setCrawled(crawled);
            crawler.setResults(results);

            return crawler;
        }

    }

    private static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private Set<String> toCrawl;
    private Set<String> crawled;
    private Set<String> results;

    private Source source;
    private BlockingQueue<String> resultUrlsQueue;
    private ResultProcessor resultProcessor;
    private CrawlingStrategy crawlingStrategy;

    private Crawler(Source source, CrawlingStrategy crawlingStrategy, BlockingQueue<String> resultUrlsQueue, ResultProcessor resultProcessor) {
        this.source = source;
        this.crawlingStrategy = crawlingStrategy;
        this.resultUrlsQueue = resultUrlsQueue;
        this.resultProcessor = resultProcessor;
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
                    if (!results.contains(resultUrl)) {
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
