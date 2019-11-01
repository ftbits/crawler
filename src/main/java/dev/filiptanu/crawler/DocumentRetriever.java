package dev.filiptanu.crawler;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class DocumentRetriever {

    public Optional<Document> retrieveDocumentFromUrl(String url, Map<String, String> cookies) throws IOException {
        Optional<Connection.Response> connectionOptional = ConnectionFactory.getResponse(url, cookies);
        if (connectionOptional.isPresent()) {
            Connection.Response response = connectionOptional.get();

            return Optional.of(response.parse());
        }

        return Optional.empty();
    }

}
