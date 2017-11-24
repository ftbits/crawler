package rocks.filip.crawler;

public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

    public ResultValueEntity(String resultValueCssQuery, ResultType resultType) {
        this.resultValueCssQuery = resultValueCssQuery;
        this.resultType = resultType;
    }

    @Override
    public String toString() {
        return "ResultValueEntity{" +
                "resultValueCssQuery='" + resultValueCssQuery + '\'' +
                ", resultType=" + resultType +
                '}';
    }

    public String getResultValueCssQuery() {
        return resultValueCssQuery;
    }

    public ResultType getResultType() {
        return resultType;
    }

}
