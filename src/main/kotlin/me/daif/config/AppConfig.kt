package me.daif.config

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import me.daif.di.appModule
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory


class AppConfig {
    lateinit var serverConfig: ServerConfig
    lateinit var dbConfig: DatabaseConfig
}

fun Application.setUpAppConfig() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    val appConfig by inject<AppConfig>()

    val serverObject = environment.config.config("ktor.server")
    val env = serverObject.property("env").getString()
    appConfig.serverConfig = ServerConfig(env=Environment.valueOf(env))

    val dbObject = environment.config.config("ktor.database")
    val dbHost = dbObject.property("dbHost").getString()
    val dbPort = dbObject.property("dbPort").getString()
    val dbName = dbObject.property("dbName").getString()
    val dbUser = dbObject.property("dbUser").getString()
    val dbPass = dbObject.property("dbPass").getString()
    val maxPoolSize = dbObject.property("maxPoolSize").getString().toInt()
    appConfig.dbConfig = DatabaseConfig(
        dbHost=dbHost,
        dbPort=dbPort,
        dbName=dbName,
        dbUser=dbUser,
        dbPass=dbPass,
        maxPoolSize=maxPoolSize,
    )

    if (appConfig.serverConfig.env != Environment.PROD) {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        root.level = ch.qos.logback.classic.Level.TRACE
    }
}
