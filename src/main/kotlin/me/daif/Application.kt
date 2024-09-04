package me.daif

import me.daif.model.PostgresProfileRepository
import me.daif.plugins.configureDatabases
import me.daif.plugins.configureRouting
import me.daif.plugins.configureSecurity
import me.daif.plugins.configureSerialization
import io.ktor.server.application.*
import me.daif.config.setUpAppConfig

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    setUpAppConfig()

    val profileRepository = PostgresProfileRepository()

    configureSerialization()
    configureSecurity()
    configureRouting(profileRepository)
    configureDatabases()
}
