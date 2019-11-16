package dev.filiptanu.crawler;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CrawlingStrategyResponse {

    private Set<String> urlsToFollow;
    private Set<String> resultUrls;

    public CrawlingStrategyResponse() {
        urlsToFollow = new HashSet<>();
        resultUrls = new HashSet<>();
    }

    public void addUrlToFollow(String url) {
        urlsToFollow.add(url);
    }
    public void addResultUrl(String url) {
        resultUrls.add(url);
    }

}
