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
        if (!((Map<String, Object>) ((Map<String, Object>) request.get("claims")).get("user_info")).get("did").equals(jwtPayload.getIss())) {
            throw new MalformedLoginJwtException("DID in original request doesn't match signature in response JWT");
        }
    }
}
