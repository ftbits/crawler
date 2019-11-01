package dev.filiptanu.crawler;

import lombok.AllArgsConstructor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static dev.filiptanu.crawler.Constants.POISON_PILL;

@AllArgsConstructor
public class ResultProcessor extends Thread {

    private static final Logger logger = Logger.getLogger(ResultProcessor.class.getName());

    private BlockingQueue<String> resultUrlsQueue;
    private ResultProcessingStrategy resultProcessingStrategy;

    @Override
    public void run() {
        while (true) {
            try {
                String url = resultUrlsQueue.poll(30, TimeUnit.SECONDS);

                logger.info("Getting result for url: " + url);

                if (POISON_PILL.name().equals(url)) {
                    break;
                }

                if (url != null) {
                    resultProcessingStrategy.processResult(url);
                }
            } catch (Exception e) {
                logger.warning(e.toString());
            }
        }

        logger.info("ResultProcessorWorker finished");
    }

}
