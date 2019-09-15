package dev.filiptanu.crawler;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

import static dev.filiptanu.crawler.Constants.POISON_PILL;

@AllArgsConstructor
public class ResultProcessorWorker extends Thread {

    private static final Logger logger = Logger.getLogger(ResultProcessorWorker.class.getName());

    private Source source;
    private BlockingQueue<String> resultUrlsQueue;
    private ResultRepository resultRepository;
    private Map<String, String> cookies;
    private DocumentRetriever documentRetriever;

    public void run() {
        while (true) {
            try {
                String url = resultUrlsQueue.poll(30, TimeUnit.SECONDS);

                if (url == POISON_PILL) {
                    break;
                }

                if (url != null) {
                    Map<String, ResultValueEntity> resultValueCssQueries = source.getResultValueEntities();

                    Map<String, String> results = new HashMap<>();
                    results.put("url", url);
                    results.put("source", source.getName());

                    Optional<Document> documentOptional = documentRetriever.retrieveDocumentFromUrl(url, cookies);
                    if (documentOptional.isPresent()) {
                        Document document = documentOptional.get();

                        resultValueCssQueries.forEach((resultKey, resultValueEntity) -> {
                            String resultValue = resultValueEntity.getResultType().extractResult(document, resultValueEntity.getResultValueCssQuery());

                            Function<String, String> resultValueCleanupStrategy = source.getResultValueCleanupStrategies().get(resultKey);

                            if (resultValueCleanupStrategy != null) {
                                resultValue = resultValueCleanupStrategy.apply(resultValue);
                            }

                            results.put(resultKey, resultValue);
                        });

                        resultRepository.saveResults(results);
                    }
                }
            } catch (InterruptedException e) {
                logger.warning(e.toString());
            } catch (IOException e) {
                logger.warning(e.toString());
            }
        }

        logger.info("ResultProcessorWorker finished");
    }

}