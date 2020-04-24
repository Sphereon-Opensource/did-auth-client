package com.sphereon.libs.did.auth.client.utils;

import com.sphereon.libs.did.auth.client.exceptions.MalformedJwtException;
import me.uport.sdk.jwt.model.JwtPayload;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeRawJwtPayload;

public class DidAuthUtils {
    public static void assertWellFormedJwtLoginRequest(JwtPayload jwtPayload) throws MalformedJwtException {
        if (jwtPayload.getReq() == null) {
            throw new MalformedJwtException("Could not retrieve original request");
        }
        Map<String, Object> request = decodeRawJwtPayload(jwtPayload.getReq());
        boolean isDidMatchingOriginalRequest = ((Map<String, Object>) ((Map<String, Object>) request.get("claims")).get("user_info")).get("did").equals(jwtPayload.getIss());
        if (!isDidMatchingOriginalRequest) {
            throw new MalformedJwtException("DID in original request doesn't match signature in response JWT");
        }
        //TODO: Use configurable value of DID
        if (!request.get("iss").equals("did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04")) {
            throw new MalformedJwtException("Issuer in the original request doesn't match Application DID");
        }
    }

    public static void assertWellFormedJwtRegistrationRequest(JwtPayload jwtPayload) throws MalformedJwtException {
        boolean isDidMatchingOriginalRequest = ((Map<String, Object>) jwtPayload.getClaims()
                .get("user_info")).get("registrationId") != null;
        if (!isDidMatchingOriginalRequest) {
            throw new MalformedJwtException("DID in original request doesn't match signature in response JWT");
        }
        //TODO: Use configurable value of DID
        if (!jwtPayload.getIss().equals("did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04")) {
            throw new MalformedJwtException("Issuer in the original request doesn't match Application DID");
        }
    }
}
