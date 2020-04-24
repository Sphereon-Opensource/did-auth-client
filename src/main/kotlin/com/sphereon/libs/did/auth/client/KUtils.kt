package com.sphereon.libs.did.auth.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.uport.sdk.core.ITimeProvider
import me.uport.sdk.jwt.JWTTools
import me.uport.sdk.jwt.model.JwtHeader
import me.uport.sdk.jwt.model.JwtPayload
import me.uport.sdk.signer.KPSigner


fun createJwtSync(
    timeProvider: ITimeProvider,
    payload: Map<String, Any>,
    issuerDid: String,
    signerSecret: String
): String = runBlocking {
    val signer = KPSigner(signerSecret)
    val res = GlobalScope.async {
        JWTTools(timeProvider).createJWT(payload, issuerDid, signer)
    }
    res.await()
}

fun verifyJwtSync(timeProvider: ITimeProvider, token: String, auth: Boolean, audience: String) = runBlocking {
    val json = Json(JsonConfiguration.Stable.copy(unquoted = true))
    val res = GlobalScope.async {
        json.stringify(JwtPayload.serializer(), JWTTools(timeProvider).verify(token, auth, audience))
    }
    res.await()
}

fun decodeRawJwtPayload(jwt: String): Map<String, Any?> {
    val (header, payload, sig) = JWTTools().decodeRaw(jwt)
    return payload
}

fun decodeJwtPayload(jwt: String): Triple<JwtHeader, JwtPayload, ByteArray> {
    return JWTTools().decode(jwt)
}


fun generateLoginPayload(recipientDid: String, callbackUrl: String): Map<String, Any> {
    return mapOf(
        "claims" to mapOf(
            "user_info" to mapOf("did" to recipientDid)
        ),
        "callback" to callbackUrl,
        "type" to "shareReq"
    )
}


fun generateRegistrationPayload(registrationId: String, callbackUrl: String): Map<String, Any> {
    return mapOf(
        "claim" to mapOf(
            "user_info"  to mapOf("registrationId" to registrationId)
        ),
        "callback" to callbackUrl
    )
}
