package me.daif

import me.daif.features.profile.domain.repository.ProfileRepository
//import me.daif.plugins.configureDatabases
import me.daif.plugins.configureRouting
import me.daif.plugins.configureSecurity
import me.daif.plugins.configureSerialization
import io.ktor.server.application.*
import me.daif.config.setUpAppConfig
import me.daif.database.DatabaseFactory
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    setUpAppConfig()

    val databaseFactory by inject<DatabaseFactory>()
    databaseFactory.connect()



    configureSerialization()
    configureSecurity()

    val profileRepository by inject<ProfileRepository>()
    configureRouting(profileRepository)
}
