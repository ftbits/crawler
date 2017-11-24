package dev.filiptanu.crawler;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        Source anhoch = new Source("anhoch", "http://www.anhoch.com");
        anhoch.addToFollowUrlCssQuery("#cat-sidemenu > li > a"); // menu categories
        anhoch.addToFollowUrlCssQuery("#cat-sidemenu > li > ul > li > a"); // menu sub-categories
        anhoch.addToFollowUrlCssQuery(".pagination > ul > li > a"); // pagination
        anhoch.addResultPageCssQuery(".product-name > a");
        anhoch.addResultValueCssQuery("title", new ResultValueEntity("#product > .box-stripes > .box-heading > h3", ResultType.TEXT));
        anhoch.addResultValueCssQuery("price", new ResultValueEntity(".price > .nm", ResultType.TEXT));
        anhoch.addResultValueCssQuery("category", new ResultValueEntity("#breadcrumbs li:nth-last-child(2)", ResultType.TEXT));
        anhoch.addResultValueCssQuery("description", new ResultValueEntity(".tab-content > div > div > pre", ResultType.TEXT));
        anhoch.addResultValueCssQuery("imageUrl", new ResultValueEntity("#product_gallery > img", ResultType.SRC));
        anhoch.addResultValueCleanupStrategy("price", resultValue -> resultValue.replaceAll("[^0-9]", ""));

        Crawler anhochCrawler = new Crawler(anhoch, new ResultRepositoryMock());

        anhochCrawler.start();
    }

}
