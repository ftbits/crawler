package dev.filiptanu.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import dev.filiptanu.crawler.ResultType;

@AllArgsConstructor
@Getter
@ToString
public class ResultValueEntity {

    private String resultValueCssQuery;
    private ResultType resultType;

}
