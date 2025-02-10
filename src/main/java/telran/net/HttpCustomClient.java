package telran.net;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class HttpCustomClient implements NetworkClient {
    private final String baseUrl;
    private final HttpClient client;

    public HttpCustomClient(String host, int port) {
        this.baseUrl = "http://" + host + ":" + port + "/";
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public String sendAndReceive(String requestType, String requestData) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + requestType))
                    .POST(BodyPublishers.ofString(requestData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Request failed", e);
        }
    }
}
