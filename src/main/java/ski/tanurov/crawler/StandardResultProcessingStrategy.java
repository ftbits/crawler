package ski.tanurov.crawler;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

@AllArgsConstructor
public class StandardResultProcessingStrategy implements ResultProcessingStrategy {

    private static final Logger logger = Logger.getLogger(StandardResultProcessingStrategy.class.getName());

    private Source source;
    private DocumentRetriever documentRetriever;
    private Map<String, String> cookies;
    private ResultRepository resultRepository;

    @Override
    public void processResult(String url) {
        try {
            Map<String, ResultValueEntity> resultValueCssQueries = source.getResultNamesResultValueEntities();

            Map<String, String> results = new HashMap<>();
            results.put("url", url);
            results.put("source", source.getName());

            Optional<Document> documentOptional = documentRetriever.retrieveDocumentFromUrl(url, cookies);
            if (documentOptional.isPresent()) {
                Document document = documentOptional.get();

                resultValueCssQueries.forEach((resultKey, resultValueEntity) -> {
                    String resultValue = resultValueEntity.getResultType().extractResult(document, resultValueEntity.getResultValueCssQuery());

                    UnaryOperator<String> resultValueCleanupStrategy = source.getResultNamesResultValueCleanupStrategies().get(resultKey);

                    if (resultValueCleanupStrategy != null) {
                        resultValue = resultValueCleanupStrategy.apply(resultValue);
                    }

                    results.put(resultKey, resultValue);
                });

                resultRepository.saveResults(results);
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

}
