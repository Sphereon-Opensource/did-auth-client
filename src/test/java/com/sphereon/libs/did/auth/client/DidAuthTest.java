package com.sphereon.libs.did.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.Configuration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;

public class DidAuthTest {
    private static final int DID_MAP_PORT = 8080;
    private static final int DID_TRANSPORT_PORT = 3000;
    private final String appDid = "<insert-app-did>";
    private final String appSecret = "<insert-app-secret>";
    private DidAuthFlow defaultDidAuthFlow;
    private DidMappingService didMappingService;
    private DidTransportsControllerApi didTransportsControllerApi;
    private DisclosureRequestService disclosureRequestService;
    private HttpClient httpClient;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        var defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setHost("localhost");
        defaultClient.setPort(DID_MAP_PORT);
        var didMapControllerApi = new DidMapControllerApi(defaultClient);
        this.didMappingService = new DidMappingService(didMapControllerApi);

        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.didTransportsControllerApi = new DidTransportsControllerApi(httpClient,
                "http://localhost:" + DID_TRANSPORT_PORT,
                new ObjectMapper());
        this.httpClient = httpClient;
        this.disclosureRequestService = new DisclosureRequestService(appDid, appSecret);
        this.defaultDidAuthFlow = new DidAuthFlow(didMappingService, didTransportsControllerApi, disclosureRequestService);
    }

    @Test
    @Ignore("This is an integration test, for more info, see Java docs")
    /**
     * In order to run this test, you must first fill out all variables with "<>" in this class
     * and have both did-mapping-ms and did-transports-ms running with a <userId> and <appId> registered in
     * did-mapping-ms with a valid push token. If everything is successful, this will push a notification
     * to your mobile.
     */
    public void testUserPush() throws IOException, InterruptedException {
        var callback = "<insert-callback-here>";
        var appId = "<insert-app-id>";
        var userId = "<insert-user-id>";
        var jwt = this.defaultDidAuthFlow.dispatchLoginRequest(appId, userId, callback);
        System.out.println(jwt);
    }
}