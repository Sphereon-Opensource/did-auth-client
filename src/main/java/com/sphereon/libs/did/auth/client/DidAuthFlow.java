package com.sphereon.libs.did.auth.client;

import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.MalformedJwtException;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.libs.did.auth.client.model.LoginRequest;
import com.sphereon.libs.did.auth.client.model.RegistrationRequest;
import com.sphereon.libs.did.auth.client.model.UserInfo;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.Configuration;
import kotlin.Triple;
import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.core.SystemTimeProvider;
import me.uport.sdk.jwt.model.JwtHeader;
import me.uport.sdk.jwt.model.JwtPayload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeJwtPayload;
import static com.sphereon.libs.did.auth.client.KUtilsKt.verifyJwtSync;
import static com.sphereon.libs.did.auth.client.utils.DidAuthUtils.assertWellFormedJwtLoginRequest;
import static com.sphereon.libs.did.auth.client.utils.DidAuthUtils.assertWellFormedJwtRegistrationRequest;

public class DidAuthFlow {
    private final DidMappingService didMappingService;
    private final DidTransportsControllerApi didTransportsControllerApi;
    private final DisclosureRequestService disclosureRequestService;
    private final ITimeProvider timeProvider;

    public DidAuthFlow(DidMappingService didMappingService, DidTransportsControllerApi didTransportsControllerApi, DisclosureRequestService disclosureRequestService, ITimeProvider timeProvider) {
        this.didMappingService = didMappingService;
        this.didTransportsControllerApi = didTransportsControllerApi;
        this.disclosureRequestService = disclosureRequestService;
        this.timeProvider = timeProvider;
    }

    public DidAuthFlow(DidMappingService didMappingService, DidTransportsControllerApi didTransportsControllerApi, DisclosureRequestService disclosureRequestService) {
        this(didMappingService, didTransportsControllerApi, disclosureRequestService, SystemTimeProvider.INSTANCE);
    }

    public DidAuthFlow(String appDid, String appSecret, String transportApiUrl, String mappingApiUrl) throws MalformedURLException {
        this(didMappingServiceFrom(mappingApiUrl), new DidTransportsControllerApi(transportApiUrl), new DisclosureRequestService(appDid, appSecret));
    }

    public String dispatchLoginRequest(String appId, String userId, String callbackUrl) throws IOException, InterruptedException, UserNotFoundException {
        var userInfo = didMappingService.getUserInfo(appId, userId);
        String disclosureRequestJwt = disclosureRequestService.createLoginDisclosureRequest(timeProvider, userInfo.getDid(), callbackUrl);
        var loginRequest = new LoginRequest(disclosureRequestJwt, userInfo.getPushToken(), userInfo.getBoxPub());
        didTransportsControllerApi.sendLoginRequest(loginRequest);
        return disclosureRequestJwt;
    }

    public String dispatchRegistrationRequest(String registrationId, String callbackUrl) throws IOException, InterruptedException {
        String disclosureRequestJwt = disclosureRequestService.createRegistrationDisclosureRequest(timeProvider, registrationId, callbackUrl);
        var registrationRequest = new RegistrationRequest(disclosureRequestJwt);
        return didTransportsControllerApi.sendRegistrationRequest(registrationRequest);
    }

    public String verifyLoginToken(String jwt) throws MalformedJwtException {
        Triple<JwtHeader, JwtPayload, byte[]> decodedJWT = decodeJwtPayload(jwt);
        JwtPayload payload = decodedJWT.getSecond();
        assertWellFormedJwtLoginRequest(payload);
        verifyJwtSync(this.timeProvider, payload.getReq(), true, disclosureRequestService.getAppDid());
        return verifyJwtSync(this.timeProvider, jwt, true, disclosureRequestService.getAppDid());
    }

    private static DidMappingService didMappingServiceFrom(final String mappingApiUrl) throws MalformedURLException {
        URL url = new URL(mappingApiUrl);
        var apiClient = Configuration.getDefaultApiClient();
        apiClient.setScheme(url.getProtocol());
        apiClient.setHost(url.getHost());
        apiClient.setPort(url.getPort() > 0 ? url.getPort() : -1); // -1 here is handled by the SDK as no port
        apiClient.setBasePath(url.getPath());
        return new DidMappingService(new DidMapControllerApi(apiClient));
    }

    public String registrationRequestIdFromToken(String token) throws MalformedJwtException {
        final Triple<JwtHeader, JwtPayload, byte[]> decodedJWT = decodeJwtPayload(token);
        final var jwtPayload = decodedJWT.getSecond();
        assertWellFormedJwtRegistrationRequest(jwtPayload);
        final var userInfoMap = (Map<String, Object>) jwtPayload.getClaims().get("user_info");
        return (String) userInfoMap.get("registrationId");
    }

    // TODO Test coverage
    public void registerDidMapping(String applicationId, String userId, UserInfo userInfo) {
        didMappingService.storeDidMapping(applicationId, userId, userInfo);
    }
}
