package rocks.filip.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

}
