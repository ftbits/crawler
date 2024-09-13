package ski.tanurov.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class Source {

    // TODO (filip): Use better variable names / add documentation on all classes

    public static class Builder {

        @AllArgsConstructor
        @Getter
        @ToString
        public static class ResultValueBuilderEntity {

            private String resultName;
            private ResultValueEntity resultValueEntity;

        }

        @AllArgsConstructor
        @Getter
        @ToString
        public static class ResultValueCleanupStrategyBuilderEntry {

            private String resultName;
            private UnaryOperator<String> resultValueCleanupStrategy;

        }

        private String name;
        private String seed;
        private List<String> toFollowUrlCssQueries;
        private List<String> resultPageCssQueries;
        private List<ResultValueBuilderEntity> resultValueBuilderEntities;
        private List<ResultValueCleanupStrategyBuilderEntry> resultValueCleanupStrategyBuilderEntries;
        private UnaryOperator<String> urlCleanupStrategy;

        public Builder(String name, String seed) {
            if (name == null) {
                throw new IllegalArgumentException("The source name must be provided.");
            }
            if (seed == null) {
                throw new IllegalArgumentException("The source seed must be provided.");
            }

            toFollowUrlCssQueries = new ArrayList<>();
            resultPageCssQueries = new ArrayList<>();
            resultValueBuilderEntities = new ArrayList<>();
            resultValueCleanupStrategyBuilderEntries = new ArrayList<>();

            this.name = name;
            this.seed = seed;
        }

        public Builder toFollowUrlCssQueries(List<String> toFollowUrlCssQueries) {
            this.toFollowUrlCssQueries.addAll(toFollowUrlCssQueries);

            return this;
        }

        public Builder resultPageCssQueries(List<String> resultPageCssQueries) {
            this.resultPageCssQueries.addAll(resultPageCssQueries);

            return this;
        }

        public Builder resultValueBuilderEntities(List<ResultValueBuilderEntity> resultValueBuilderEntities) {
            this.resultValueBuilderEntities.addAll(resultValueBuilderEntities);

            return this;
        }

        public Builder resultValueCleanupStrategyBuilderEntries(List<ResultValueCleanupStrategyBuilderEntry> resultValueCleanupStrategyBuilderEntries) {
            this.resultValueCleanupStrategyBuilderEntries.addAll(resultValueCleanupStrategyBuilderEntries);

            return this;
        }

        public Builder urlCleanupStrategy(UnaryOperator<String> urlCleanupStrategy) {
            this.urlCleanupStrategy = urlCleanupStrategy;

            return this;
        }

        public Source build() {
            Source source = new Source(name, seed);

            source.setToFollowUrlCssQueries(toFollowUrlCssQueries);
            source.setResultPageCssQueries(resultPageCssQueries);
            source.setResultNamesResultValueEntities(
                    resultValueBuilderEntities.stream()
                        .collect(Collectors.toMap(ResultValueBuilderEntity::getResultName, ResultValueBuilderEntity::getResultValueEntity))
            );
            if (urlCleanupStrategy != null) {
                resultValueCleanupStrategyBuilderEntries.add(new Source.Builder.ResultValueCleanupStrategyBuilderEntry("url", urlCleanupStrategy));
            }
            source.setResultNamesResultValueCleanupStrategies(
                    resultValueCleanupStrategyBuilderEntries.stream()
                            .collect(Collectors.toMap(ResultValueCleanupStrategyBuilderEntry::getResultName, ResultValueCleanupStrategyBuilderEntry::getResultValueCleanupStrategy))
            );

            return source;
        }
    }

    private String name;
    private String seed;
    private List<String> toFollowUrlCssQueries;
    private List<String> resultPageCssQueries;
    private Map<String, ResultValueEntity> resultNamesResultValueEntities;
    private Map<String, UnaryOperator<String>> resultNamesResultValueCleanupStrategies;

    private Source(String name, String seed) {
        this.name = name;
        this.seed = seed;
    }

    public Optional<UnaryOperator<String>> getUrlCleanupStrategy() {
        return Optional.ofNullable(resultNamesResultValueCleanupStrategies.get("url"));
    }

}
