package rocks.filip.crawler;

import java.util.function.BiFunction;
import org.jsoup.nodes.Document;

public enum ResultType {

    TEXT((document, cssQuery) -> document.select(cssQuery).text()),
    SRC((document, cssQuery) -> document.select(cssQuery).attr("src"));

    private BiFunction<Document, String, String> resultExtractor;

    ResultType(BiFunction<Document, String, String> resultExtractor) {
        this.resultExtractor = resultExtractor;
    }

    public String extractResult(Document document, String cssQuery) {
        return resultExtractor.apply(document, cssQuery);
    }

}
