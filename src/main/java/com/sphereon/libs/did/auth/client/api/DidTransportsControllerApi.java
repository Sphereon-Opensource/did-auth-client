package com.sphereon.libs.did.auth.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.libs.did.auth.client.exceptions.FailedTransportsException;
import com.sphereon.libs.did.auth.client.model.LoginRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DidTransportsControllerApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private HttpClient httpClient;
    private final String apiBaseUrl;
    private ObjectMapper objectMapper;

    public DidTransportsControllerApi(HttpClient httpClient, String apiBaseUrl, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.apiBaseUrl = apiBaseUrl;
        this.objectMapper = objectMapper;
    }

    public DidTransportsControllerApi(String apiBaseUrl) {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.apiBaseUrl = apiBaseUrl;
        this.objectMapper = new ObjectMapper();
    }

    public void sendLoginRequest(LoginRequest loginRequest) throws IOException, InterruptedException, FailedTransportsException {
        var loginVarPath = "/login";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + loginVarPath))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginRequest)))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            var msg = "Dispatch to DID Transports MS failed with HTTP Status code " + resp.statusCode()
                    + " and error message: " + resp.body();
            throw new FailedTransportsException(msg);
        }
    }
}
