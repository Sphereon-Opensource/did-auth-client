package com.sphereon.libs.did.auth.client;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.createJwtSync;

public class DidAuthFlow {

    public String createJWT(Map<String, ?> payload, String issuerDid, String issuerSecret){
        return createJwtSync(payload, issuerDid, issuerSecret);
    }
}
