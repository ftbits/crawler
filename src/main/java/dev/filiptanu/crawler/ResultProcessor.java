package dev.filiptanu.crawler;

import lombok.AllArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

@AllArgsConstructor
public class ResultProcessor extends Thread {

    private static final Logger logger = Logger.getLogger(ResultProcessor.class.getName());

    private Crawler crawler;
    private ResultRepository resultRepository;
    private Semaphore semaphore;

    public void run() {
        while (!crawler.isFinished()) {
            try {
                String url = crawler.getResultUrlsQueue().poll(30, TimeUnit.SECONDS);

                if (url != null) {
                    Map<String, ResultValueEntity> resultValueCssQueries = crawler.getSource().getResultValueEntities();

                    Map<String, String> results = new HashMap<>();
                    results.put("url", url);
                    results.put("source", crawler.getSource().getName());

                    Optional<Connection.Response> connectionOptional = ConnectionFactory.getResponse(url, crawler.getCookies(), crawler.getSource().isUseProxy());
                    if (connectionOptional.isPresent()) {
                        Document document = connectionOptional.get().parse();

                        if (document != null) {
                            resultValueCssQueries.forEach((resultKey, resultValueEntity) -> {
                                String resultValue = resultValueEntity.getResultType().extractResult(document, resultValueEntity.getResultValueCssQuery());

                                Function<String, String> resultValueCleanupStrategy = crawler.getSource().getResultValueCleanupStrategies().get(resultKey);

                                if (resultValueCleanupStrategy != null) {
                                    resultValue = resultValueCleanupStrategy.apply(resultValue);
                                }

                                results.put(resultKey, resultValue);
                            });

                            resultRepository.saveResults(results);

                            if (crawler.getResultUrlsQueue().isEmpty()) {
                                semaphore.release();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.warning(e.toString());
            } catch (IOException e) {
                logger.warning(e.toString());
            }
        }

        logger.info("ResultProcessor finished");
    }

}