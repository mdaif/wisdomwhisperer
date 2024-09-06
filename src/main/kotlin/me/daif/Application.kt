package me.daif

import io.ktor.http.*
import me.daif.features.profile.domain.repository.ProfileRepository
import me.daif.plugins.configureRouting
import me.daif.plugins.configureSecurity
import me.daif.plugins.configureSerialization
import io.ktor.server.application.*
import me.daif.config.setUpAppConfig
import me.daif.database.DatabaseFactory
import me.daif.features.auth.jwkprovider.JwtVerifier
import org.koin.ktor.ext.inject
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    setUpAppConfig()

    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        status(HttpStatusCode.BadRequest) { call, status ->
            val message = call.response.headers["X-Error-Message"] ?: "Bad Request"
            call.respondText(text = "$status: $message", status = status)
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "$status: 404 Not found", status = status)
        }
    }

    val databaseFactory by inject<DatabaseFactory>()
    val jwtVerifier by inject<JwtVerifier>()
    databaseFactory.connect()

    configureSerialization()
    configureSecurity(jwtVerifier)

    val profileRepository by inject<ProfileRepository>()
    configureRouting(profileRepository)
}
