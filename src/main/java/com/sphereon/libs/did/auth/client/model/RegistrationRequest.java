package com.sphereon.libs.did.auth.client.model;

public class RegistrationRequest {
    private final String jwt;

    public RegistrationRequest(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
