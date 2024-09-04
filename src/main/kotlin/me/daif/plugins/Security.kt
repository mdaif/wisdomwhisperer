package me.daif.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.concurrent.TimeUnit


fun Application.configureSecurity() {
    val cognitoPoolId = System.getenv("COGNITO_USER_POOL_ID")
    val awsRegion = System.getenv("AWS_REGION")
    val issuer = "https://cognito-idp.$awsRegion.amazonaws.com/$cognitoPoolId"
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "WisdomWhisperer"
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Forbidden, "Token is not valid or has expired")
            }
        }
    }
}

