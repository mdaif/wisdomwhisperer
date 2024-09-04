package me.daif.config

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import me.daif.di.appModule
import org.koin.core.module.Module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory


class AppConfig {
    lateinit var serverConfig: ServerConfig
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

    if (appConfig.serverConfig.env != Environment.PROD) {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        root.level = ch.qos.logback.classic.Level.TRACE
    }
}

data class ServerConfig(
    val env: Environment
)


enum class Environment {
    DEV, TEST, STAGING, PROD
}