package com.sphereon.libs.did.auth.client;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class DidAuthTest {

    @Test
    //@Ignore("This is an integration test, for more info, see Java docs")
    /*
     * In order to run this test, you must first fill out all variables with "<>" in this method
     * and have both did-mapping-ms and did-transports-ms running with a <userId> and <appId> registered in
     * did-mapping-ms with a valid push token. If everything is successful, this will push a notification
     * to your mobile.
     */
    public void testUserPush() throws IOException, InterruptedException {
        var didMappingUrl = "<insert-did-mapping-url>";
        var didTransportsUrl = "<insert-did-transports-url>";
        var appDid = "<insert-app-did>";
        var appSecret = "<insert-app-secret>";
        var callback = "<insert-callback>";
        var appId = "<insert-app-id>";
        var userId = "<insert-user-id>";
        var didAuthFlow = new DidAuthFlow(appDid, appSecret, didTransportsUrl, didMappingUrl);
        var jwt = didAuthFlow.dispatchLoginRequest(appId, userId, callback);
        System.out.println(jwt);
    }
}