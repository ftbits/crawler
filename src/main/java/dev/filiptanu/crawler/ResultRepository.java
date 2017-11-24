package dev.filiptanu.crawler;

import java.util.Map;

public interface ResultRepository {

    void saveResults(Map<String, String> results);

}
