package com.sphereon.libs.did.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.FailedTransportsException;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.Configuration;
import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.jwt.InvalidJWTException;
import me.uport.sdk.jwt.model.JwtPayload;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.spongycastle.util.encoders.DecoderException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

public class DidAuthFlowTest {
    private DidAuthFlow didAuthFlow;
    private DidAuthFlow defaultDidAuthFlow;
    private DidMappingService didMappingService;
    private DidTransportsControllerApi didTransportsControllerApi;
    private DisclosureRequestService disclosureRequestService;

    private final String appDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04";
    private final String appSecret = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0";
    private static final int DID_MAP_PORT = 8090;
    private static final int DID_TRANSPORT_PORT = 3001;

    @ClassRule
    public static WireMockClassRule wireMockRuleMapping = new WireMockClassRule(wireMockConfig()
            .port(DID_MAP_PORT)
            .usingFilesUnderDirectory("src/test/resources/wiremock-mapping"));

    @ClassRule
    public static WireMockClassRule wireMockRuleTransports = new WireMockClassRule(wireMockConfig()
            .port(DID_TRANSPORT_PORT)
            .usingFilesUnderDirectory("src/test/resources/wiremock-transports"));

    @Before
    public void setUp() {
        var defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setHost("localhost");
        defaultClient.setPort(DID_MAP_PORT);
        var didMapControllerApi = new DidMapControllerApi(defaultClient);
        this.didMappingService = new DidMappingService(didMapControllerApi);

        var httpClient = HttpClient.newHttpClient();
        this.didTransportsControllerApi = new DidTransportsControllerApi(httpClient,
                "http://localhost:" + DID_TRANSPORT_PORT,
                new ObjectMapper());

        this.disclosureRequestService = new DisclosureRequestService(appDid, appSecret);
        this.defaultDidAuthFlow = new DidAuthFlow(didMappingService, didTransportsControllerApi, disclosureRequestService);
    }

    @Test
    public void verifyJwtShouldReturnValidPayloadIfJwtIsValid() {
        didAuthFlow = getDidAuthFlowAtTime(1573814893000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dOemtpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        JwtPayload payload = didAuthFlow.verifyLoginToken(jwt);
        assert (payload != null);
        assertEquals(payload.getIss(), "did:ethr:0x6d09b03d3113db0989abce8e2d4cbf03b7d89079");
    }

    @Test(expected = DecoderException.class)
    public void verifyJwtShouldThrowExceptionWhenJwtIsNotValid() {
        didAuthFlow = getDidAuthFlowAtTime(1573814893000L);
        var jwt = "invalidjwtheader.invalidjwtbody.invalidjwtsig";
        didAuthFlow.verifyLoginToken(jwt);
    }

    @Test(expected = InvalidJWTException.class)
    public void verifyJwtShouldThrowExceptionWhenResponseIsExpired() {
        didAuthFlow = getDidAuthFlowAtTime(1573914893000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dOemtpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        didAuthFlow.verifyLoginToken(jwt);
    }

    @Test
    public void dispatchLoginRequestShouldWorkForValidLogin() throws IOException, InterruptedException {
        HttpResponse<String> resp = defaultDidAuthFlow.dispatchLoginRequest("test-application", "test-user", "test-callback");
        assertEquals(resp.statusCode(), 200);
    }

    @Test(expected = FailedTransportsException.class)
    public void dispatchLoginRequestShouldFailForInvalidBoxPub() throws IOException, InterruptedException {
        defaultDidAuthFlow.dispatchLoginRequest("invalid-test-application", "invalid-test-user", "test-callback");
    }

    @Test(expected = UserNotFoundException.class)
    public void dispatchLoginShouldFailForNonExistentUser() throws IOException, InterruptedException {
        defaultDidAuthFlow.dispatchLoginRequest("test-application", "non-existent-user", "test-callback");
    }

    private DidAuthFlow getDidAuthFlowAtTime(long time) {
        var timeProvider = new ITimeProvider() {
            @Override
            public long nowMs() {
                return time;
            }
        };
        return new DidAuthFlow(didMappingService, didTransportsControllerApi, disclosureRequestService, timeProvider);
    }

}
