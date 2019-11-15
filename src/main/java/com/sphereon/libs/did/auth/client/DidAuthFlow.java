package com.sphereon.libs.did.auth.client;

import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import com.sphereon.libs.did.auth.client.model.LoginRequest;
import com.sphereon.libs.did.auth.client.model.UserInfo;
import kotlin.Triple;
import me.uport.sdk.jwt.model.JwtHeader;
import me.uport.sdk.jwt.model.JwtPayload;

import java.io.IOException;
import java.net.http.HttpResponse;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeJwtPayload;
import static com.sphereon.libs.did.auth.client.KUtilsKt.verifyJwtSync;
import static com.sphereon.libs.did.auth.client.utils.DidAuthUtils.wellFormedJwtLoginRequest;

public class DidAuthFlow {
    private final DidMappingService didMappingService;
    private final DidTransportsControllerApi didTransportsControllerApi;
    private final DisclosureRequestService disclosureRequestService;

    public DidAuthFlow(DidMappingService didMappingService, DidTransportsControllerApi didTransportsControllerApi, DisclosureRequestService disclosureRequestService) {
        this.didMappingService = didMappingService;
        this.didTransportsControllerApi = didTransportsControllerApi;
        this.disclosureRequestService = disclosureRequestService;
    }

    public HttpResponse<String> dispatchLoginRequest(String appId, String userId) throws IOException, InterruptedException {
        UserInfo userInfo = didMappingService.getUserInfo(appId, userId);
        String jwt = disclosureRequestService.createDisclosureRequest(userInfo.getDid());
        var loginRequest = new LoginRequest(jwt, userInfo.getBoxPub(), userInfo.getPushToken());
        return didTransportsControllerApi.sendLoginRequest(loginRequest);
    }

    public JwtPayload verifyLoginToken(String jwt) throws MalformedLoginJwtException {
        Triple<JwtHeader, JwtPayload, byte[]> decodedJWT = decodeJwtPayload(jwt);
        JwtPayload payload = decodedJWT.getSecond();
        if (wellFormedJwtLoginRequest(payload)) {
            return verifyJwtSync(jwt, true, disclosureRequestService.getAppDid());
        } else {
            throw new MalformedLoginJwtException("Did in request doesn't match signature");
        }
    }
}
