package me.daif.features.auth.jwkprovider

import com.auth0.jwk.JwkProviderBuilder
import java.util.concurrent.TimeUnit
import io.ktor.server.auth.jwt.*

class AwsCognitoVerifier : JwtVerifier {
    private fun getIssuer(): String {
        val cognitoPoolId = System.getenv("COGNITO_USER_POOL_ID")
        val awsRegion = System.getenv("AWS_REGION")
        val issuer = "https://cognito-idp.$awsRegion.amazonaws.com/$cognitoPoolId"
        return issuer
    }
    override fun verifyJwt(config: JWTAuthenticationProvider.Config) {
        val issuer = getIssuer()
        val jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

        config.verifier(jwkProvider, issuer)
    }
}