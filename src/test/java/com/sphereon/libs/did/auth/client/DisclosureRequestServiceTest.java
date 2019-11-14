package com.sphereon.libs.did.auth.client;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.sphereon.libs.did.auth.client.KUtilsKt.decodeRawJwtPayload;
import static com.sphereon.libs.did.auth.client.KUtilsKt.generateClaims;
import static org.junit.Assert.assertEquals;

public class DisclosureRequestServiceTest {
    private final String appDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04";
    private final String appSecret = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0";
    private DisclosureRequestService disclosureRequestService;

    @Before
    public void setUp() {
        disclosureRequestService = new DisclosureRequestService(appDid, appSecret);
    }

    @Test
    public void claimsShouldGenerateMap() {
        Map<String, ?> claimsMap = generateClaims("user-did");
        Map claims = (Map) claimsMap.get("claims");
        Map userInfo = (Map) claims.get("user_info");
        assertEquals(userInfo.get("did"), "user-did");
    }

    @Test
    public void createDisclosureRequestShouldCreateCorrectJwtPayload() {
        String jwt = disclosureRequestService.createDisclosureRequest("user-did");
        Map<String, ?> jwtPayload = decodeRawJwtPayload(jwt);
        assertEquals(jwtPayload.get("iss"), "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04");
        assertEquals(((Map) ((Map) jwtPayload.get("claims")).get("user_info")).get("did"), "user-did");
    }
}
