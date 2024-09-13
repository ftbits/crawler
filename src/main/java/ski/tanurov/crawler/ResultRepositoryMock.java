package ski.tanurov.crawler;

import java.util.Map;

public class ResultRepositoryMock implements ResultRepository {

    @Override
    public void saveResults(Map<String, String> results) {
        System.out.println("Saving results: " + results);
    }

}