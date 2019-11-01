package dev.filiptanu.crawler;

public interface CrawlingStrategy {

    CrawlingStrategyResponse crawl(String url);

}
