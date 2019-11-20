package com.sphereon.libs.did.auth.client;

import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import com.sphereon.libs.did.auth.client.model.LoginRequest;
import kotlin.Triple;
import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.core.SystemTimeProvider;
import me.uport.sdk.jwt.model.JwtHeader;
import me.uport.sdk.jwt.model.JwtPayload;

import java.io.IOException;
import java.net.http.HttpResponse;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeJwtPayload;
import static com.sphereon.libs.did.auth.client.KUtilsKt.verifyJwtSync;
import static com.sphereon.libs.did.auth.client.utils.DidAuthUtils.assertWellFormedJwtLoginRequest;

public class DidAuthFlow {
    private final DidMappingService didMappingService;
    private final DidTransportsControllerApi didTransportsControllerApi;
    private final DisclosureRequestService disclosureRequestService;
    private ITimeProvider timeProvider;

    public DidAuthFlow(DidMappingService didMappingService, DidTransportsControllerApi didTransportsControllerApi, DisclosureRequestService disclosureRequestService) {
        this.didMappingService = didMappingService;
        this.didTransportsControllerApi = didTransportsControllerApi;
        this.disclosureRequestService = disclosureRequestService;
        this.timeProvider = SystemTimeProvider.INSTANCE;
    }

    public DidAuthFlow(DidMappingService didMappingService, DidTransportsControllerApi didTransportsControllerApi, DisclosureRequestService disclosureRequestService, ITimeProvider timeProvider) {
        this.didMappingService = didMappingService;
        this.didTransportsControllerApi = didTransportsControllerApi;
        this.disclosureRequestService = disclosureRequestService;
        this.timeProvider = timeProvider;
    }

    public HttpResponse<String> dispatchLoginRequest(String appId, String userId, String callbackUrl) throws IOException, InterruptedException {
        var userInfo = didMappingService.getUserInfo(appId, userId);
        String jwt = disclosureRequestService.createDisclosureRequest(userInfo.getDid(), callbackUrl);
        var loginRequest = new LoginRequest(jwt, userInfo.getBoxPub(), userInfo.getPushToken());
        return didTransportsControllerApi.sendLoginRequest(loginRequest);
    }

    public JwtPayload verifyLoginToken(String jwt) throws MalformedLoginJwtException {
        Triple<JwtHeader, JwtPayload, byte[]> decodedJWT = decodeJwtPayload(jwt);
        JwtPayload payload = decodedJWT.getSecond();
        assertWellFormedJwtLoginRequest(payload);
        return verifyJwtSync(this.timeProvider, jwt, true, disclosureRequestService.getAppDid());
    }
}
