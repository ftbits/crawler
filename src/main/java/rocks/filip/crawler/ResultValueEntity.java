package rocks.filip.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import rocks.filip.crawler.ResultType;

@AllArgsConstructor
@ToString
@Getter
public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

}
