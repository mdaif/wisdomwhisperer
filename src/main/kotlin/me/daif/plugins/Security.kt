package me.daif.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.daif.features.auth.jwkprovider.JwtVerifier


fun Application.configureSecurity(jwtVerifier: JwtVerifier) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "WisdomWhisperer"

            jwtVerifier.verifyJwt(this)
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
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

