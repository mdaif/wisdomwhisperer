package me.daif.features.auth.jwkprovider

import com.auth0.jwk.JwkProvider as OAuthJwkProvider
import com.auth0.jwk.Jwk
import com.auth0.jwk.SigningKeyNotFoundException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap

class TestJwkProvider: JwkProvider  {
    override fun getIssuer(): String {
        return "https://test-issuer"
    }

    override fun createProvider(): OAuthJwkProvider {
        val jwkProvider = MockJwkProvider()
        return jwkProvider
    }
}


class MockJwkProvider : OAuthJwkProvider {

    // Cache of the generated keys
    private val jwksCache = ConcurrentHashMap<String, Jwk>()

    // Initialize the provider with a mock key
    init {
        val keyPair = generateRSAKeyPair()
        val publicKey = keyPair.public as RSAPublicKey

        // Create a JWK representation of the public key
        val jwk = Jwk.fromValues(mapOf(
            "kty" to "RSA",
            "kid" to "test-key-id", // Key ID
            "alg" to "RS256", // Algorithm used
            "use" to "sig", // Use for signature
            "n" to Base64.getUrlEncoder().encodeToString(publicKey.modulus.toByteArray()), // Modulus
            "e" to Base64.getUrlEncoder().encodeToString(publicKey.publicExponent.toByteArray()) // Exponent
        ))

        // Add the JWK to the cache
        jwksCache["test-key-id"] = jwk
    }

    // Function to retrieve the JWK by key ID
    override fun get(keyId: String): Jwk {
        return jwksCache[keyId] ?: throw SigningKeyNotFoundException("Key not found", null)
    }

    // Helper function to generate an RSA Key Pair
    private fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }
}

