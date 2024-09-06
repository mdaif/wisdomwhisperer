package me.daif.features.auth.jwkprovider

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

private const val ISSUER = "http://localhost"
private const val AUDIENCE = "http://localhost"
private const val SECRET = "SHHHHHHH"

object Rsa256Pair {
    private val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA").also {
        it.initialize(2048, SecureRandom())
    }
    private val keypair: KeyPair = generator.genKeyPair()

    val privateKey: RSAPrivateKey = keypair.private as RSAPrivateKey
    val publicKey: RSAPublicKey = keypair.public as RSAPublicKey

}


fun createJwtToken(username: String): String {
    return JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(Algorithm.RSA256(Rsa256Pair.publicKey, Rsa256Pair.privateKey))
}



class TestJwtVerifier : JwtVerifier {

    override fun verifyJwt(config: JWTAuthenticationProvider.Config) {
        config.verifier(
            JWT.require(Algorithm.RSA256(Rsa256Pair.publicKey, Rsa256Pair.privateKey))
                .withAudience(AUDIENCE)
                .withIssuer(ISSUER)
                .build()
        )
    }


}