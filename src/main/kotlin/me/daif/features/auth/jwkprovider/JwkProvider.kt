package me.daif.features.auth.jwkprovider

import com.auth0.jwk.JwkProvider as OAuthJwkProvider


interface JwkProvider {
    fun createProvider(): OAuthJwkProvider
    fun getIssuer(): String
}