package com.sphereon.libs.did.auth.client.utils;

import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import me.uport.sdk.jwt.model.JwtPayload;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeRawJwtPayload;

public class DidAuthUtils {
    public static void assertWellFormedJwtLoginRequest(JwtPayload jwtPayload) throws MalformedLoginJwtException {
        if (jwtPayload.getReq() == null) {
            throw new MalformedLoginJwtException("Could not retrieve original request");
        }
        Map<String, Object> request = decodeRawJwtPayload(jwtPayload.getReq());
        boolean isDidMatchingOriginalRequest = ((Map<String, Object>) ((Map<String, Object>) request.get("claims")).get("user_info")).get("did").equals(jwtPayload.getIss());
        if (!isDidMatchingOriginalRequest) {
            throw new MalformedLoginJwtException("DID in original request doesn't match signature in response JWT");
        }
        //TODO: Use configurable value of DID
        if (!request.get("iss").equals("did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04")) {
            throw new MalformedLoginJwtException("Issuer in the original request doesn't match Application DID");
        }
    }
}
