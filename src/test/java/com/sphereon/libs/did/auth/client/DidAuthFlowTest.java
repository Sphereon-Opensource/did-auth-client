package com.sphereon.libs.did.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import me.uport.sdk.core.ITimeProvider;
import me.uport.sdk.jwt.model.JwtPayload;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.util.encoders.DecoderException;

import java.net.http.HttpClient;

import static org.junit.Assert.assertEquals;

public class DidAuthFlowTest {
    private DidAuthFlow didAuthFlow;
    private final String appDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04";
    private final String appSecret = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0";

    @Before
    public void setUp() {
        var didMapControllerApi = new DidMapControllerApi();
        var httpClient = HttpClient.newHttpClient();
        var didTransportsControllerApi = new DidTransportsControllerApi(httpClient, "http://localhost:8080", new ObjectMapper());
        var disclosureRequestService = new DisclosureRequestService(appDid, appSecret);
        var didMappingService = new DidMappingService(didMapControllerApi);
        var timeProvider = new ITimeProvider() {
            @Override
            public long nowMs() {
                return 1573814893000L;
            }
        };
        didAuthFlow = new DidAuthFlow(didMappingService, didTransportsControllerApi, disclosureRequestService, timeProvider);
    }


    @Test
    public void verifyJwtShouldReturnValidPayloadIfJwtIsValid() {
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dOemtpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        JwtPayload payload = didAuthFlow.verifyLoginToken(jwt);
        assert (payload != null);
        assertEquals(payload.getIss(), "did:ethr:0x6d09b03d3113db0989abce8e2d4cbf03b7d89079");
    }

    @Test(expected = DecoderException.class)
    public void verifyJwtShouldThrowExceptionWhenJwtIsNotValid() {
        var jwt = "invalidjwtheader.invalidjwtbody.invalidjwtsig";
        didAuthFlow.verifyLoginToken(jwt);
    }

}
