package dev.filiptanu.crawler;

import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@ToString
@Getter
public class Source {

    private String name;
    private String seed;
    private List<String> toFollowUrlCssQueries;
    private List<String> resultPageCssQueries;
    private Map<String, ResultValueEntity> resultValueEntities;
    private Map<String, UnaryOperator<String>> resultValueCleanupStrategies;

    private Source() {
        toFollowUrlCssQueries = new ArrayList<>();
        resultPageCssQueries = new ArrayList<>();
        resultValueEntities = new HashMap<>();
        resultValueCleanupStrategies = new HashMap<>();
    }

    public Source(String name) {
        this();

        this.name = name;
    }

    public Source(String name, String seed) {
        this(name);

        this.seed = seed;
    }

    public void setUrlCleanupStrategy(UnaryOperator<String> urlCleanupStrategy) {
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

    public void addResultValueCleanupStrategy(String resultKey, UnaryOperator<String> resultValueCleanupStrategy) {
        resultValueCleanupStrategies.put(resultKey, resultValueCleanupStrategy);
    }

}