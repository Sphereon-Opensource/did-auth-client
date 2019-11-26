package com.sphereon.libs.did.auth.client.model;

public class UserInfo {
    private final String did;
    private final String boxPub;
    private final String pushToken;

    public UserInfo(String did, String boxPub, String pushToken) {
        this.did = did;
        this.boxPub = boxPub;
        this.pushToken = pushToken;
    }

    public String getBoxPub() {
        return boxPub;
    }

    public String getPushToken() {
        return pushToken;
    }

    public String getDid() {
        return did;
    }
}
