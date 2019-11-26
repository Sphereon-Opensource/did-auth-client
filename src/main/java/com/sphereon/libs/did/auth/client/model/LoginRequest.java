package com.sphereon.libs.did.auth.client.model;

public class LoginRequest {
    private final String jwt;
    private final String pushToken;
    private final String boxPub;

    public LoginRequest(String jwt, String pushToken, String boxPub) {
        this.jwt = jwt;
        this.pushToken = pushToken;
        this.boxPub = boxPub;
    }

    public String getJwt() {
        return jwt;
    }

    public String getPushToken() {
        return pushToken;
    }

    public String getBoxPub() {
        return boxPub;
    }
}
