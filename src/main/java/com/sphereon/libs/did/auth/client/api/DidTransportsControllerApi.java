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

    public DidTransportsControllerApi(HttpClient httpClient, String apiBaseUrl, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.apiBaseUrl = apiBaseUrl;
        this.objectMapper = objectMapper;
    }

    public HttpResponse<String> sendLoginRequest(LoginRequest loginRequest) throws IOException, InterruptedException {
        String loginVarPath = "/login";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + loginVarPath))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginRequest)))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
