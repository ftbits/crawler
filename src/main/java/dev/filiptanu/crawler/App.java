package dev.filiptanu.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        Source source = new Source("anhoch", "http://www.anhoch.com");
//        source.setUrlCleanupStrategy(url -> url.trim());
        source.addToFollowUrlCssQuery("#cat-sidemenu > li > ul > li > a"); // menu sub-categories
        source.addToFollowUrlCssQuery("#cat-sidemenu > li > a"); // menu categories
        source.addToFollowUrlCssQuery(".pagination > ul > li > a"); // pagination
        source.addResultPageCssQuery(".product-name > a");
        source.addResultValueCssQuery("title", new ResultValueEntity("#product > .box-stripes > .box-heading > h3", ResultType.TEXT));
        source.addResultValueCssQuery("price", new ResultValueEntity(".price > .nm", ResultType.TEXT));
        source.addResultValueCssQuery("externalCategory", new ResultValueEntity("#breadcrumbs li:nth-last-child(2)", ResultType.TEXT));
        source.addResultValueCssQuery("description", new ResultValueEntity(".tab-content > div > div > pre", ResultType.TEXT));
        source.addResultValueCssQuery("imageUrl", new ResultValueEntity("#product_gallery > img", ResultType.SRC));
        source.addResultValueCleanupStrategy("price", resultValue -> resultValue.replaceAll("[^0-9]", ""));

        BlockingQueue<String> resultUrlsQueue = new LinkedBlockingQueue<>();
        ResultRepository resultRepository = results -> logger.info("Saving results: " + results);
        Map<String, String> cookies = new HashMap<>();
        DocumentRetriever documentRetriever = new DocumentRetriever();
        ResultProcessingStrategy resultProcessingStrategy = new StandardResultProcessingStrategy(source, documentRetriever, cookies, resultRepository);
        ResultProcessor resultProcessor = new ResultProcessor(resultUrlsQueue, resultProcessingStrategy);
        CrawlingStrategy crawlingStrategy = new StandardCrawlingStrategy(documentRetriever, cookies, source);
        Crawler crawler = new Crawler(source, resultUrlsQueue, resultProcessor, crawlingStrategy);

        crawler.start();
    }

}
