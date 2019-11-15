package com.sphereon.libs.did.auth.client;

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

    public String createDisclosureRequest(String recipientDid, String callbackUrl) {
        Map<String, Object> claims = generatePayload(recipientDid, callbackUrl);
        return createJwtSync(claims, appDid, appSecret);
    }

    public String getAppDid() {
        return appDid;
    }

}
