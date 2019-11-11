package dev.filiptanu.crawler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        Source source = new Source.Builder("anhoch", "http://www.anhoch.com")
                .toFollowUrlCssQueries(
                        Arrays.asList(
                                "#cat-sidemenu > li > ul > li > a",
                                "#cat-sidemenu > li > a",
                                ".pagination > ul > li > a"
                        )
                )
                .resultPageCssQueries(
                        Arrays.asList(
                                ".product-name > a"
                        )
                )
                .resultValueBuilderEntities(
                        Arrays.asList(
                                new Source.Builder.ResultValueBuilderEntity(
                                        "title",
                                        new ResultValueEntity(
                                                "#product > .box-stripes > .box-heading > h3",
                                                ResultType.TEXT
                                        )
                                ),
                                new Source.Builder.ResultValueBuilderEntity(
                                        "price",
                                        new ResultValueEntity(
                                                ".price > .nm",
                                                ResultType.TEXT
                                        )
                                ),
                                new Source.Builder.ResultValueBuilderEntity(
                                        "externalCategory",
                                        new ResultValueEntity(
                                                "#breadcrumbs li:nth-last-child(2)",
                                                ResultType.TEXT
                                        )
                                ),
                                new Source.Builder.ResultValueBuilderEntity(
                                        "description",
                                        new ResultValueEntity(
                                                ".tab-content > div > div > pre",
                                                ResultType.TEXT
                                        )
                                ),
                                new Source.Builder.ResultValueBuilderEntity(
                                        "imageUrl",
                                        new ResultValueEntity(
                                                "#product_gallery > img",
                                                ResultType.SRC
                                        )
                                )
                        )
                )
                .resultValueCleanupStrategyBuilderEntries(
                        Arrays.asList(
                            new Source.Builder.ResultValueCleanupStrategyBuilderEntry(
                                    "price",
                                    resultValue -> resultValue.replaceAll("[^0-9]", "")
                            )
                        )
                )
                .urlCleanupStrategy(String::trim)
                .build();

        Crawler crawler = new Crawler.Builder(source, results -> logger.info("Saving results: " + results))
                .build();

        crawler.start();
    }

}
