package ski.tanurov.crawler;

public interface CrawlingStrategy {

    CrawlingStrategyResponse crawl(String url);

}
