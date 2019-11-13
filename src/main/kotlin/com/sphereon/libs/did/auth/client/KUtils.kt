package com.sphereon.libs.did.auth.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.uport.sdk.jwt.JWTTools
import me.uport.sdk.signer.KPSigner


fun createJwtSync(payload: Map<String, Any>, issuerDid: String, signerPK: String): String = runBlocking {
    val signer = KPSigner(signerPK)
    val res = GlobalScope.async {
        JWTTools().createJWT(payload, issuerDid, signer)
    }
    res.await()
}

fun verifyJwtSync(token: String, auth: Boolean, audience: String) = runBlocking {
    val res = GlobalScope.async {
        JWTTools().verify(token, auth, audience)
    }
    res.await()
}
