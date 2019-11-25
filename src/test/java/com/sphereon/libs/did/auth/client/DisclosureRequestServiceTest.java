package com.sphereon.libs.did.auth.client;

import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.core.SystemTimeProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeRawJwtPayload;
import static com.sphereon.libs.did.auth.client.KUtilsKt.generatePayload;
import static org.junit.Assert.assertEquals;

public class DisclosureRequestServiceTest {
    private final String appDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04";
    private final String appSecret = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0";
    private final ITimeProvider timeProvider = SystemTimeProvider.INSTANCE;
    private DisclosureRequestService disclosureRequestService;

    @Before
    public void setUp() {
        disclosureRequestService = new DisclosureRequestService(appDid, appSecret);
    }

    @Test
    public void generatePayloadShouldGenerateCorrectMap() {
        Map<String, Object> payload = generatePayload("user-did", "callback-url");
        Map claims = (Map) payload.get("claims");
        String callbackUrl = (String) payload.get("callback");
        Map userInfo = (Map) claims.get("user_info");
        assertEquals(userInfo.get("did"), "user-did");
        assertEquals(callbackUrl, "callback-url");
    }

    @Test
    public void createDisclosureRequestShouldCreateCorrectJwtPayload() {
        String jwt = disclosureRequestService.createDisclosureRequest(timeProvider,"user-did", "callback-url");
        Map<String, Object> jwtPayload = decodeRawJwtPayload(jwt);
        assertEquals(jwtPayload.get("iss"), "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04");
        assertEquals(((Map<String, Object>) ((Map<String,Object>) jwtPayload.get("claims")).get("user_info")).get("did"), "user-did");
    }
}
