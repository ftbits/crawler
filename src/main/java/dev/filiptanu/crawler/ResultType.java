package dev.filiptanu.crawler;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;

@AllArgsConstructor
public enum ResultType {

    TEXT((document, cssQuery) -> document.select(cssQuery).text()),
    SRC((document, cssQuery) -> document.select(cssQuery).attr("src")),
    DEFAULTIMAGE((document, cssQuery) -> document.select(cssQuery).attr("defaultimage")),
    HREF((document, cssQuery) -> document.select(cssQuery).attr("href"));

    private BiFunction<Document, String, String> resultExtractor;

    public String extractResult(Document document, String cssQuery) {
        return resultExtractor.apply(document, cssQuery);
    }

}