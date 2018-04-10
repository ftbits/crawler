package rocks.filip.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Source {

    private String name;
    private String seed;
    private boolean hasRelativeUrls;
    private List<String> toFollowUrlCssQueries;
    private List<String> resultPageCssQueries;
    private Map<String, ResultValueEntity> resultValueEntities;
    private Map<String, Function<String, String>> resultValueCleanupStrategies;

    public Source(String name, String seed) {
        this.name = name;
        this.seed = seed;
        this.hasRelativeUrls = false;
        toFollowUrlCssQueries = new ArrayList<String>();
        resultPageCssQueries = new ArrayList<String>();
        resultValueEntities = new HashMap<>();
        resultValueCleanupStrategies = new HashMap<>();
    }

    public void setHasRelativeUrls(boolean hasRelativeUrls) {
        this.hasRelativeUrls = hasRelativeUrls;
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

    public void addResultValueCleanupStrategy(String resultKey, Function<String, String> resultValueCleanupStrategy) {
        resultValueCleanupStrategies.put(resultKey, resultValueCleanupStrategy);
    }

    public String getName() {
        return name;
    }

    public String getSeed() {
        return seed;
    }

    public boolean hasRelativeUrls() {
        return hasRelativeUrls;
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

    public Map<String, Function<String, String>> getResultValueCleanupStrategies() {
        return resultValueCleanupStrategies;
    }

    @Override
    public String toString() {
        return "Source{" +
                "name='" + name + '\'' +
                ", seed='" + seed + '\'' +
                ", hasRelativeUrls=" + hasRelativeUrls +
                ", toFollowUrlCssQueries=" + toFollowUrlCssQueries +
                ", resultPageCssQueries=" + resultPageCssQueries +
                ", resultValueEntities=" + resultValueEntities +
                ", resultValueCleanupStrategies=" + resultValueCleanupStrategies +
                '}';
    }

}