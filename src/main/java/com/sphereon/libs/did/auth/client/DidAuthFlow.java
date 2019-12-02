package com.sphereon.libs.did.auth.client;

import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.libs.did.auth.client.model.LoginRequest;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.Configuration;
import kotlin.Triple;
import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.core.SystemTimeProvider;
import me.uport.sdk.jwt.model.JwtHeader;
import me.uport.sdk.jwt.model.JwtPayload;

import java.io.IOException;

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

    public DidAuthFlow(String appId, String appSecret , String transportApiBaseUrl, String mappingApiBaseUrl) {
        var apiClient = Configuration.getDefaultApiClient();
        apiClient.setHost(mappingApiBaseUrl);
        apiClient.setPort(8090);
        DidMapControllerApi didMapControllerApi = new DidMapControllerApi(apiClient);
        didMappingService = new DidMappingService(didMapControllerApi);
        didTransportsControllerApi = new DidTransportsControllerApi(transportApiBaseUrl);
        this.disclosureRequestService = new DisclosureRequestService(appId, appSecret);
    }

    public String dispatchLoginRequest(String appId, String userId, String callbackUrl) throws IOException, InterruptedException, UserNotFoundException {
        var userInfo = didMappingService.getUserInfo(appId, userId);
        String disclosureRequestJwt = disclosureRequestService.createDisclosureRequest(timeProvider, userInfo.getDid(), callbackUrl);
        var loginRequest = new LoginRequest(disclosureRequestJwt, userInfo.getPushToken(), userInfo.getBoxPub());
        didTransportsControllerApi.sendLoginRequest(loginRequest);
        return disclosureRequestJwt;
    }

    public String verifyLoginToken(String jwt) throws MalformedLoginJwtException {
        Triple<JwtHeader, JwtPayload, byte[]> decodedJWT = decodeJwtPayload(jwt);
        JwtPayload payload = decodedJWT.getSecond();
        assertWellFormedJwtLoginRequest(payload);
        verifyJwtSync(this.timeProvider, payload.getReq(), true, disclosureRequestService.getAppDid());
        return verifyJwtSync(this.timeProvider, jwt, true, disclosureRequestService.getAppDid());
    }
}
