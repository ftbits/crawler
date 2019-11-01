package dev.filiptanu.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import dev.filiptanu.crawler.ResultType;

@AllArgsConstructor
@ToString
@Getter
public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

}
