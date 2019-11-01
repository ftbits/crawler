package rocks.filip.crawler;

public interface CrawlingStrategy {

    CrawlingStrategyResponse crawl(String url);

}
