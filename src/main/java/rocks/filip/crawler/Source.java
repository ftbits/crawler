package rocks.filip.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Source {

    private String name;
    private String seed;
    private List<String> toFollowUrlCssQueries;
    private List<String> resultPageCssQueries;
    private Map<String, ResultValueEntity> resultValueEntities;
    private Map<String, ResultValueCleanupStrategy> resultValueCleanupStrategies;

    public Source(String name, String seed) {
        this.name = name;
        this.seed = seed;
        toFollowUrlCssQueries = new ArrayList<String>();
        resultPageCssQueries = new ArrayList<String>();
        resultValueEntities = new HashMap<>();
        resultValueCleanupStrategies = new HashMap<>();
    }

    public void addToFollowUrlCssQuery(String cssQuery) {
        toFollowUrlCssQueries.add(cssQuery);
    }

    public void addResultPageCssQuery(String cssQuery) {
        resultPageCssQueries.add(cssQuery);
    }

    public void addResultValueCssQuery(String resultKey, ResultValueEntity resultValueEntity) {
        resultValueEntities.put(resultKey, resultValueEntity);
    }

    public void addResultValueCleanupStrategy(String resultKey, ResultValueCleanupStrategy resultValueCleanupStrategy) {
        resultValueCleanupStrategies.put(resultKey, resultValueCleanupStrategy);
    }

    public String getName() {
        return name;
    }

    public String getSeed() {
        return seed;
    }

    public List<String> getToFollowUrlCssQueries() {
        return toFollowUrlCssQueries;
    }

    public List<String> getResultPageCssQueries() {
        return resultPageCssQueries;
    }

    public Map<String, ResultValueEntity> getResultValueEntities() {
        return resultValueEntities;
    }

    public Map<String, ResultValueCleanupStrategy> getResultValueCleanupStrategies() {
        return resultValueCleanupStrategies;
    }
    
}
