package com.sphereon.libs.did.auth.client;

import me.uport.sdk.core.ITimeProvider;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.createJwtSync;
import static com.sphereon.libs.did.auth.client.KUtilsKt.generatePayload;

public class DisclosureRequestService {
    private final String appDid;
    private final String appSecret;

    public DisclosureRequestService(String appDid, String appSecret) {
        this.appDid = appDid;
        this.appSecret = appSecret;
    }

    public String createDisclosureRequest(ITimeProvider timeProvider, String recipientDid, String callbackUrl) {
        Map<String, Object> claims = generatePayload(recipientDid, callbackUrl);
        return createJwtSync(timeProvider, claims, appDid, appSecret);
    }

    public String createDisclosureRequest(ITimeProvider timeProvider, String appId, String registrationId, String callbackUrl) {
        Map<String, Object> claims = generatePayload(appId, registrationId, callbackUrl);
        return createJwtSync(timeProvider, claims, appDid, appSecret);
    }

    public String getAppDid() {
        return appDid;
    }

}
