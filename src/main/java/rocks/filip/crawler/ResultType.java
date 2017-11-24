package rocks.filip.crawler;

import org.jsoup.nodes.Document;

public enum ResultType {

    TEXT((document, cssQuery) -> document.select(cssQuery).text()),
    SRC((document, cssQuery) -> document.select(cssQuery).attr("src"));

    private ResultExtractor resultExtractor;

    ResultType(ResultExtractor resultExtractor) {
        this.resultExtractor = resultExtractor;
    }

    public String extractResult(Document document, String cssQuery) {
        return resultExtractor.extractResult(document, cssQuery);
    }

}
