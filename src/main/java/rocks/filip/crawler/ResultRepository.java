package rocks.filip.crawler;

import java.util.Map;

public interface ResultRepository {

    void saveResults(Map<String, String> results);

}
