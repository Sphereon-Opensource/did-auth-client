package com.sphereon.libs.did.auth.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.libs.did.auth.client.model.LoginRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DidTransportsControllerApi {
    private HttpClient httpClient;
    private final String apiBaseUrl;
    private ObjectMapper objectMapper;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    public DidTransportsControllerApi(HttpClient httpClient, String apiBaseUrl, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.apiBaseUrl = apiBaseUrl;
        this.objectMapper = objectMapper;
    }

    public HttpResponse<String> sendLoginRequest(LoginRequest loginRequest) throws IOException, InterruptedException {
        var loginVarPath = "/login";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + loginVarPath))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginRequest)))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
