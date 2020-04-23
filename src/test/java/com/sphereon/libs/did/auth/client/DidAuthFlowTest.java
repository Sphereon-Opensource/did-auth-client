package com.sphereon.libs.did.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.libs.did.auth.client.exceptions.FailedTransportsException;
import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.Configuration;
import me.uport.sdk.jwt.InvalidJWTException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.spongycastle.util.encoders.DecoderException;

import java.io.IOException;
import java.net.http.HttpClient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.*;

public class DidAuthFlowTest {
    private static final int DID_MAP_PORT = 8795;
    private static final int DID_TRANSPORT_PORT = 3001;
    @ClassRule
    public static WireMockClassRule wireMockRuleMapping = new WireMockClassRule(wireMockConfig()
            .port(DID_MAP_PORT)
            .usingFilesUnderDirectory("src/test/resources/wiremock-mapping"));
    @ClassRule
    public static WireMockClassRule wireMockRuleTransports = new WireMockClassRule(wireMockConfig()
            .port(DID_TRANSPORT_PORT)
            .usingFilesUnderDirectory("src/test/resources/wiremock-transports"));
    private final String appDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04";
    private final String appSecret = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0";
    private DidAuthFlow didAuthFlow;
    private DidAuthFlow defaultDidAuthFlow;
    private DidMappingService didMappingService;
    private DidTransportsControllerApi didTransportsControllerApi;
    private DisclosureRequestService disclosureRequestService;

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
        didAuthFlow = getDidAuthFlowAtTime(1573739069000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dOemtpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        String payload = didAuthFlow.verifyLoginToken(jwt);
        assert (payload != null);
        assertEquals("{iss:\"did:ethr:0x6d09b03d3113db0989abce8e2d4cbf03b7d89079\",iat:1573738493,nbf:null,sub:null,aud:\"did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04\",exp:1573824893,callback:null,type:shareResp,net:null,act:null,requested:null,verified:null,permissions:null,req:eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0NzAsImV4cCI6MTU3MzczOTA3MCwiY2xhaW1zIjp7InVzZXJfaW5mbyI6eyJkaWQiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkiLCJuYW1lIjp7ImVzc2VudGlhbCI6dHJ1ZSwicmVhc29uIjoidGVzdCJ9LCJlbWFpbCI6eyJlc3NlbnRpYWwiOnRydWUsInJlYXNvbiI6ImIifX19LCJwZXJtaXNzaW9ucyI6WyJub3RpZmljYXRpb25zIl0sImNhbGxiYWNrIjoiaHR0cHM6Ly9mNDBkZjM1Zi5uZ3Jvay5pby9jYWxsYmFjayIsInR5cGUiOiJzaGFyZVJlcSIsImlzcyI6ImRpZDpldGhyOjB4ODhlZDY5NGZmZTkyNDRlMjk5M2QyOTMyNjM4YTVjNzM2MzcxZmMwNCJ9.doNoghwbcU77OHGL8lIH2I-_7nAauCn_5GwdTbO4BWol3Pm7KS0TumLb2GWfemviNtg0XVS_jISLEhw2NnALMwA,nad:null,dad:null,own:{name:Gabriel,email:Test@test.nl},capabilities:[eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTYwNTI3NDQ5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6Im5vdGlmaWNhdGlvbnMiLCJ2YWx1ZSI6ImFybjphd3M6c25zOnVzLXdlc3QtMjoxMTMxOTYyMTY1NTg6ZW5kcG9pbnQvR0NNL3VQb3J0LzMzYTA5Y2U4LWZjNzMtMzYxZS04NGM2LTg3NzE1YTNlNGI1ZCIsImlzcyI6ImRpZDpldGhyOjB4NmQwOWIwM2QzMTEzZGIwOTg5YWJjZThlMmQ0Y2JmMDNiN2Q4OTA3OSJ9.c28X7Wq_2SdpFqTv2MgjGqFN9AahiZJdfsItIbwtOldOR0g16ruIY15v7VG50i8d6YIpOuKrldcfE3A3GFOFNQA],claim:null,ctl:null,reg:null,rel:null,fct:null,acc:null}",
                payload);
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

    @Test(expected = MalformedLoginJwtException.class)
    public void verifyJwtShouldThrowExceptionWhenOriginalRequestDIDDoesntMatchResponse() {
        didAuthFlow = getDidAuthFlowAtTime(1573824892000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dNVEFpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        didAuthFlow.verifyLoginToken(jwt);
    }

    @Test(expected = MalformedLoginJwtException.class)
    public void verifyJwtShouldThrowExceptionWhenIssuerInRequestDoesntMatchAppDID() {
        didAuthFlow = getDidAuthFlowAtTime(1573824892000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBOekFzSW1WNGNDSTZNVFUzTXpjek9UQTNNQ3dpWTJ4aGFXMXpJanA3SW5WelpYSmZhVzVtYnlJNmV5SmthV1FpT2lKa2FXUTZaWFJvY2pvd2VEWmtNRGxpTUROa016RXhNMlJpTURrNE9XRmlZMlU0WlRKa05HTmlaakF6WWpka09Ea3dOemtpTENKdVlXMWxJanA3SW1WemMyVnVkR2xoYkNJNmRISjFaU3dpY21WaGMyOXVJam9pZEdWemRDSjlMQ0psYldGcGJDSTZleUpsYzNObGJuUnBZV3dpT25SeWRXVXNJbkpsWVhOdmJpSTZJbUlpZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhScGIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5bU5EQmtaak0xWmk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOU0o5LmRvTm9naHdiY1U3N09IR0w4bElIMkktXzduQWF1Q25fNUd3ZFRiTzRCV29sM1BtN0tTMFR1bUxiMkdXZmVtdmlOdGcwWFZTX2pJU0xFaHcyTm5BTE13QSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTNNemcwT1RNc0ltVjRjQ0k2TVRZd05USTNORFE1TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00yTFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5jMjhYN1dxXzJTZHBGcVR2Mk1nakdxRk45QWFoaVpKZGZzSXRJYnd0T2xkT1IwZzE2cnVJWTE1djdWRzUwaThkNllJcE91S3JsZGNmRTNBM0dGT0ZOUUEiXSwiYm94UHViIjoiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        didAuthFlow.verifyLoginToken(jwt);
    }

    @Test(expected = MalformedLoginJwtException.class)
    public void verifyJwtShouldThrowExceptionWhenRequestIsEmpty() {
        didAuthFlow = getDidAuthFlowAtTime(1573824892000L);
        var jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM3Mzg0OTMsImV4cCI6MTU3MzgyNDg5MywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCIsImVtYWlsIjoiVGVzdEB0ZXN0Lm5sIn0sInJlcSI6bnVsbCwiY2FwYWJpbGl0aWVzIjpbImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSkZVekkxTmtzdFVpSjkuZXlKcFlYUWlPakUxTnpNM016ZzBPVE1zSW1WNGNDSTZNVFl3TlRJM05EUTVNeXdpWVhWa0lqb2laR2xrT21WMGFISTZNSGc0T0dWa05qazBabVpsT1RJME5HVXlPVGt6WkRJNU16STJNemhoTldNM016WXpOekZtWXpBMElpd2lkSGx3WlNJNkltNXZkR2xtYVdOaGRHbHZibk1pTENKMllXeDFaU0k2SW1GeWJqcGhkM002YzI1ek9uVnpMWGRsYzNRdE1qb3hNVE14T1RZeU1UWTFOVGc2Wlc1a2NHOXBiblF2UjBOTkwzVlFiM0owTHpNellUQTVZMlU0TFdaak56TXRNell4WlMwNE5HTTJMVGczTnpFMVlUTmxOR0kxWkNJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE5tUXdPV0l3TTJRek1URXpaR0l3T1RnNVlXSmpaVGhsTW1RMFkySm1NRE5pTjJRNE9UQTNPU0o5LmMyOFg3V3FfMlNkcEZxVHYyTWdqR3FGTjlBYWhpWkpkZnNJdElid3RPbGRPUjBnMTZydUlZMTV2N1ZHNTBpOGQ2WUlwT3VLcmxkY2ZFM0EzR0ZPRk5RQSJdLCJib3hQdWIiOiJTZkNWZmtMcGZmVnFHVm85N3V6bEtQdnNreDV0SE5yU0h4UnJRL2NNZ3lnPSIsImlzcyI6ImRpZDpldGhyOjB4NmQwOWIwM2QzMTEzZGIwOTg5YWJjZThlMmQ0Y2JmMDNiN2Q4OTA3OSJ9.x1nN7w8XcWHCsXm8VotGol_biWwy0zraBM7nKPLIJSWsoAGkWqNAmtQm4LdiDqwcVPHoeSi068I4XFc5HUIk_gE";
        didAuthFlow.verifyLoginToken(jwt);
    }

    @Test
    public void dispatchLoginRequestShouldWorkForValidLogin() throws IOException, InterruptedException {
        didAuthFlow = getDidAuthFlowAtTime(1573914893000L);
        String requestJwt = didAuthFlow.dispatchLoginRequest("test-application", "test-user", "test-callback");
        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJjbGFpbXMiOnsidXNlcl9pbmZvIjp7ImRpZCI6ImRpZDpldGhyOjB4ODhlZDY5NGZmZTkyNDRlMjk5M2QyOTMyNjM4YTVjNzM2MzcxZmMwNCJ9fSwiY2FsbGJhY2siOiJ0ZXN0LWNhbGxiYWNrIiwidHlwZSI6InNoYXJlUmVxIiwiaWF0IjoxNTczOTE0ODkzLCJleHAiOjE1NzM5MTUxOTMsImlzcyI6ImRpZDpldGhyOjB4ODhlZDY5NGZmZTkyNDRlMjk5M2QyOTMyNjM4YTVjNzM2MzcxZmMwNCJ9.MYgg0Et4fPnxbX0mZv0wOizaJ9a7Lih8Cpv4Oy_fb6Fiaj3tCJNOs6EM___AoLIsBxxE_y-hdHNq0yXKYD6ZMAA", requestJwt);
    }

    @Test(expected = FailedTransportsException.class)
    public void dispatchLoginRequestShouldFailForInvalidBoxPub() throws IOException, InterruptedException {
        defaultDidAuthFlow.dispatchLoginRequest("invalid-test-application", "invalid-test-user", "test-callback");
    }

    @Test(expected = UserNotFoundException.class)
    public void dispatchLoginShouldFailForNonExistentUser() throws IOException, InterruptedException {
        defaultDidAuthFlow.dispatchLoginRequest("test-application", "non-existent-user", "test-callback");
    }

    @Test
    public void dispatchRegistrationRequest() throws IOException, InterruptedException {
        didAuthFlow = getDidAuthFlowAtTime(1573914893000L);
        String qrCode = didAuthFlow.dispatchRegistrationRequest("test-application", "test-callback");
        assertNotNull(qrCode);
        assertTrue(qrCode.length() > 36);
        assertEquals("data:image/png;charset=utf-8;base64,", qrCode.substring(0, 36));
    }

    private DidAuthFlow getDidAuthFlowAtTime(long time) {
        return new DidAuthFlow(didMappingService, didTransportsControllerApi, disclosureRequestService, () -> time);
    }

}
