package me.daif.features.auth.jwkprovider

import io.ktor.server.auth.jwt.*


interface JwtVerifier {
    fun verifyJwt(config: JWTAuthenticationProvider.Config)
}