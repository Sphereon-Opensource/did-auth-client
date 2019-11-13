package com.sphereon.libs.did.auth.client

import org.junit.Test

class KUtilsTest {

    @Test
    fun `check if createJwtSync function works`() {
        val payload =  mapOf(
                "claims" to mapOf("name" to "R Daneel Olivaw")
        )
        val privateKey = "2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0"
        val issuerDid = "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04"
        val jwt = createJwtSync(payload, issuerDid, privateKey)
        assert(jwt.isNotEmpty())
        println(jwt)
    }
}