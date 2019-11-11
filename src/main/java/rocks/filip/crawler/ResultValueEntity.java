package rocks.filip.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

}
