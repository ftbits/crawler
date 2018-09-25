package dev.filiptanu.crawler;

import java.util.*;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Source {

    private String name;
    private String seed;
    private boolean useProxy;
    private List<String> toFollowUrlCssQueries;
    private List<String> resultPageCssQueries;
    private Map<String, ResultValueEntity> resultValueEntities;
    private Map<String, Function<String, String>> resultValueCleanupStrategies;

    public Source(String name, String seed, boolean useProxy) {
        this.name = name;
        this.seed = seed;
        this.useProxy = useProxy;
        toFollowUrlCssQueries = new ArrayList<>();
        resultPageCssQueries = new ArrayList<>();
        resultValueEntities = new HashMap<>();
        resultValueCleanupStrategies = new HashMap<>();
    }

    public void setUrlCleanupStrategy(Function<String, String> urlCleanupStrategy) {
        resultValueCleanupStrategies.put("url", urlCleanupStrategy);
    }

    public Optional<Function<String, String>> getUrlCleanupStrategy() {
        return Optional.ofNullable(resultValueCleanupStrategies.get("url"));
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

}