package rocks.filip.crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class App {

    public static void main(String[] args) throws IOException {
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
        Map<String, String> cookies = new HashMap<>();
        ResultRepository resultRepository = results -> System.out.println("Saving results: " + results);
        ResultProcessorWorker resultProcessorWorker = new ResultProcessorWorker(source, resultUrlsQueue, resultRepository, cookies);
        Crawler crawler = new Crawler(source, resultUrlsQueue, resultProcessorWorker, cookies);

        crawler.start();
    }

}