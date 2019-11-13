import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.uport.sdk.jwt.JWTTools
import me.uport.sdk.signer.KPSigner

fun main(args: Array<String>) {
    val signer = KPSigner("2106b0925c0b7486d3474ea0521f0a8750992902c7a13f02498e4066da3cf0f0")
    runBlocking {
        val res = GlobalScope.async {
            JWTTools().createJWT(
                mapOf(
                    "claims" to mapOf("name" to "R Daneel Olivaw")
                ), "did:ethr:0x88ed694ffe9244e2993d2932638a5c736371fc04", signer
            )
        }
        println(res.await())
    }
}