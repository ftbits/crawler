package dev.filiptanu.crawler;

import org.jsoup.nodes.Document;

public interface ResultExtractor {

    String extractResult(Document document, String cssQuery);

}
