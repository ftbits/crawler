package ski.tanurov.crawler;

import java.util.Map;

public interface ResultRepository {

    void saveResults(Map<String, String> results);

}
