package rocks.filip.crawler;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;

@AllArgsConstructor
public enum ResultType {

    TEXT((document, cssQuery) -> document.select(cssQuery).text()),
    SRC((document, cssQuery) -> document.select(cssQuery).attr("src")),
    DEFAULTIMAGE((document, cssQuery) -> document.select(cssQuery).attr("defaultimage"));

    private BiFunction<Document, String, String> resultExtractor;

    public String extractResult(Document document, String cssQuery) {
        return resultExtractor.apply(document, cssQuery);
    }

}