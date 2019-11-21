package com.sphereon.libs.did.auth.client

import me.uport.sdk.core.ITimeProvider
import me.uport.sdk.core.SystemTimeProvider
import me.uport.sdk.jwt.model.JwtPayload
import org.junit.Test

class KUtilsTest {
    //TODO: revise tests once we have working token examples
    @Test
    fun `createJwtSync function should return non-emptystring`() {
        val payload = mapOf(
            "requested" to "name"
        )
        val privateKey = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0"
        val issuerDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04"
        val jwt = createJwtSync(SystemTimeProvider, payload, issuerDid, privateKey)
        assert(jwt.isNotEmpty())
    }

    @Test
    fun `decodeJwtSync should return correct payload`() {
        val jwt =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJyZXF1ZXN0ZWQiOiJuYW1lIiwiaWF0IjoxNTczNjU5Mjk3LCJleHAiOjE1NzM2NTk1OTcsImlzcyI6ImRpZDpldGhyOjB4ODhlZDY5NGZmZTkyNDRlMjk5M2QyOTMyNjM4YTVjNzM2MzcxZmMwNCJ9.qQgSTxRBbNrTXxtkF7AysvsENgNlPOjWcWr9o3SRewB680CvQWXLjsdd3Afb-Z5PsbvqbFcI0jp-mcLCEMtPzQA"
        val payload = decodeRawJwtPayload(jwt)
        assert(payload["iss"] == "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04")
    }

    @Test
    fun `verify signed jwt response`() {
        val token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NkstUiJ9.eyJpYXQiOjE1NzM2NTI1MDMsImV4cCI6MTU3MzczODkwMywiYXVkIjoiZGlkOmV0aHI6MHg4OGVkNjk0ZmZlOTI0NGUyOTkzZDI5MzI2MzhhNWM3MzYzNzFmYzA0IiwidHlwZSI6InNoYXJlUmVzcCIsIm93biI6eyJuYW1lIjoiR2FicmllbCJ9LCJyZXEiOiJleUowZVhBaU9pSktWMVFpTENKaGJH" +
                    "Y2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTJOVEkwTWpRc0ltVjRjQ0k2TVRVM016WTFNekF5TkN3aWNtVnhkV1Z6ZEdWa0lqcGJJbTVoYldVaVhTd2lZMnhoYVcxeklqcDdJblpsY21sbWFXRmliR1VpT25zaWRYTmxjbDlwYm1adklqcDdJblJ2Y0dsalgybGtJam94ZlgxOUxDSndaWEp0YVhOemFXOXVjeUk2V3lKdWIzUnBabWxqWVhSc" +
                    "GIyNXpJbDBzSW1OaGJHeGlZV05ySWpvaWFIUjBjSE02THk5a05XUmhPV1V6WWk1dVozSnZheTVwYnk5allXeHNZbUZqYXlJc0luUjVjR1VpT2lKemFHRnlaVkpsY1NJc0ltbHpjeUk2SW1ScFpEcGxkR2h5T2pCNE9EaGxaRFk1TkdabVpUa3lORFJsTWprNU0yUXlPVE15TmpNNFlUVmpOek0yTXpjeFptTXdOQ0o5LnZGaW5FSmI2T3JXbzJtNi13eFg0Qn" +
                    "VRdU5Ud1o3aWlER1pQNlY2N3ZUdDZzSVFDc0k3eVk3bVJndGVINFVCekpNQkhyWW1iMTNQMElGLVg4V2tTX1lRQSIsImNhcGFiaWxpdGllcyI6WyJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5rc3RVaUo5LmV5SnBZWFFpT2pFMU56TTJOVEkxTURNc0ltVjRjQ0k2TVRZd05URTRPRFV3TXl3aVlYVmtJam9pWkdsa09tVjBhSEk2TUhnNE9" +
                    "HVmtOamswWm1abE9USTBOR1V5T1RrelpESTVNekkyTXpoaE5XTTNNell6TnpGbVl6QTBJaXdpZEhsd1pTSTZJbTV2ZEdsbWFXTmhkR2x2Ym5NaUxDSjJZV3gxWlNJNkltRnlianBoZDNNNmMyNXpPblZ6TFhkbGMzUXRNam94TVRNeE9UWXlNVFkxTlRnNlpXNWtjRzlwYm5RdlIwTk5MM1ZRYjNKMEx6TXpZVEE1WTJVNExXWmpOek10TXpZeFpTMDROR00y" +
                    "TFRnM056RTFZVE5sTkdJMVpDSXNJbWx6Y3lJNkltUnBaRHBsZEdoeU9qQjRObVF3T1dJd00yUXpNVEV6WkdJd09UZzVZV0pqWlRobE1tUTBZMkptTUROaU4yUTRPVEEzT1NKOS5pWGI5SDNyVjFpQU85dXBPd2d4OVYtM0Q0TDZSQW5FV3MtbGxUeVExT2JNMy1aeFFsUk83UU4tX0NUT0RSMF8wY2k2ellWMXhIYlU5ZnZuUEVCalBuUUEiXSwiYm94UHViI" +
                    "joiU2ZDVmZrTHBmZlZxR1ZvOTd1emxLUHZza3g1dEhOclNIeFJyUS9jTWd5Zz0iLCJpc3MiOiJkaWQ6ZXRocjoweDZkMDliMDNkMzExM2RiMDk4OWFiY2U4ZTJkNGNiZjAzYjdkODkwNzkifQ.clP8vRbF-7vemXb7oPjin0fUCmHNruOUBwXYCo2aspHbPehdF2BtwFB1_mMiBGOqhcqAR6TuCEe0cr6yxeJLAQA"
        val auth = true
        val audience = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04"
        val payload: JwtPayload = verifyJwtSync(TestTimeProvider, token, auth, audience)
    }

    object TestTimeProvider : ITimeProvider {
        override fun nowMs() = 1573737903000L
    }
}
