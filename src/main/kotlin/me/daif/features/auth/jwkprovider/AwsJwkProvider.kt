package me.daif.features.auth.jwkprovider

import com.auth0.jwk.JwkProviderBuilder
import java.util.concurrent.TimeUnit
import com.auth0.jwk.JwkProvider as OAuthJwkProvider

class AwsJwkProvider: JwkProvider {
    override fun getIssuer(): String {
        val cognitoPoolId = System.getenv("COGNITO_USER_POOL_ID")
        val awsRegion = System.getenv("AWS_REGION")
        val issuer = "https://cognito-idp.$awsRegion.amazonaws.com/$cognitoPoolId"
        return issuer
    }

    override fun createProvider(): OAuthJwkProvider {
        val issuer = getIssuer()

        val jwkProvider = JwkProviderBuilder(issuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        return jwkProvider
    }
}
