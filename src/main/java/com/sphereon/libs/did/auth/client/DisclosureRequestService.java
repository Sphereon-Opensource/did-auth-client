package com.sphereon.libs.did.auth.client;

import me.uport.sdk.core.ITimeProvider;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.*;

public class DisclosureRequestService {
    private final String appDid;
    private final String appSecret;

    public DisclosureRequestService(String appDid, String appSecret) {
        this.appDid = appDid;
        this.appSecret = appSecret;
    }

    public String createLoginDisclosureRequest(ITimeProvider timeProvider, String recipientDid, String callbackUrl) {
        Map<String, Object> claims = generateLoginPayload(recipientDid, callbackUrl);
        return createJwtSync(timeProvider, claims, appDid, appSecret);
    }

    public String createRegistrationDisclosureRequest(ITimeProvider timeProvider, String registrationId, String callbackUrl) {
        Map<String, Object> claims = generateRegistrationPayload(registrationId, callbackUrl);
        return createJwtSync(timeProvider, claims, appDid, appSecret);
    }

    public String getAppDid() {
        return appDid;
    }

}
