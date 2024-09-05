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
    val serverObject = environment.config.config("ktor.server")
    val env = serverObject.property("env").getString()
    if (Environment.valueOf(env) != Environment.TEST) {
        // The installation and module configurations happens in tests
        // via the `startKoin` function

        install(Koin) {
            slf4jLogger()
            modules(appModule)
        }
    }

    val appConfig by inject<AppConfig>()

    appConfig.serverConfig = ServerConfig(env=Environment.valueOf(env))

    val dbObject = environment.config.config("ktor.database")
    val dbHost = dbObject.property("dbHost").getString()
    val dbPort = dbObject.property("dbPort").getString()
    val dbName = dbObject.property("dbName").getString()
    val dbUser = dbObject.property("dbUser").getString()
    val dbPass = dbObject.property("dbPass").getString()

    // Hikari
    val dbPoolName = dbObject.property("maxPoolSize").getString()
    val dbDriverClassName = dbObject.property("dbDriverClassName").getString()
    val maxPoolSize = dbObject.property("maxPoolSize").getString().toInt()
    val minimumIdle = dbObject.property("minimumIdle").getString().toInt()
    val idleTimeout = dbObject.property("idleTimeout").getString().toLong()
    val connectionTimeout = dbObject.property("connectionTimeout").getString().toLong()
    val maxLifetime = dbObject.property("maxLifetime").getString().toLong()

    appConfig.dbConfig = DatabaseConfig(
        dbHost = dbHost,
        dbPort = dbPort,
        dbName = dbName,
        dbUser = dbUser,
        dbPass = dbPass,
        dbPoolName = dbPoolName,
        dbDriverClassName = dbDriverClassName,
        maxPoolSize = maxPoolSize,
        minimumIdle = minimumIdle,
        idleTimeout = idleTimeout,
        connectionTimeout = connectionTimeout,
        maxLifetime = maxLifetime,
    )

    if (appConfig.serverConfig.env != Environment.PROD) {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        root.level = ch.qos.logback.classic.Level.TRACE
    }
}
