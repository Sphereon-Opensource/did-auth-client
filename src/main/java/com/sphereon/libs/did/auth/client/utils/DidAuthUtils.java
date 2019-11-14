package com.sphereon.libs.did.auth.client.utils;

import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import me.uport.sdk.jwt.model.JwtPayload;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeRawJwtPayload;

public class DidAuthUtils {
    public static boolean wellFormedJwtLoginRequest(JwtPayload jwtPayload) throws MalformedLoginJwtException {
        if (jwtPayload.getReq() == null) {
            throw new MalformedLoginJwtException("Could not retrieve original request");
        }
        Map<String, ?> request = decodeRawJwtPayload(jwtPayload.getReq());
        return ((Map) ((Map) request.get("claims")).get("user_info")).get("did").equals(jwtPayload.getIss());
    }
}
