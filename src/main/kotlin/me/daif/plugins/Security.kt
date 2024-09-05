package me.daif.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.daif.features.auth.jwkprovider.JwkProvider


fun Application.configureSecurity(jwkProvider: JwkProvider) {
    val issuer = jwkProvider.getIssuer()
    val provider = jwkProvider.createProvider()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "WisdomWhisperer"
            verifier(provider, issuer) {
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

