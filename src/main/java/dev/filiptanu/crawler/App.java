package dev.filiptanu.crawler;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        Source source = new Source("anhoch", "http://www.anhoch.com", true);
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

        Crawler crawler = new Crawler(source, results -> System.out.println("Saving results: " + results));

        crawler.start();
    }

}