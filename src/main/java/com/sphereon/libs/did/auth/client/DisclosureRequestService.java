package com.sphereon.libs.did.auth.client;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.createJwtSync;
import static com.sphereon.libs.did.auth.client.KUtilsKt.generateClaims;

public class DisclosureRequestService {
    private final String appDid;
    private final String appSecret;

    public DisclosureRequestService(String appDid, String appSecret) {
        this.appDid = appDid;
        this.appSecret = appSecret;
    }

    public String createDisclosureRequest(String recipientDid) {
        Map<String, Object> claims = generateClaims(recipientDid);
        return createJwtSync(claims, appDid, appSecret);
    }

    public String getAppDid() {
        return appDid;
    }

}
