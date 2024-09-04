package me.daif.database

import me.daif.config.AppConfig
import org.jetbrains.exposed.sql.Database


class DatabaseFactoryImp(appConfig: AppConfig): DatabaseFactory {
    // appConfig is provided via Koin DI magic.
    private val dbConfig = appConfig.dbConfig

    override fun connect() {
        val dbHost: String = dbConfig.dbHost
        val dbPort: String = dbConfig.dbPort
        val dbName: String = dbConfig.dbName
        val dbUser: String = dbConfig.dbUser
        val dbPass: String = dbConfig.dbPass

        Database.connect(
            "jdbc:postgresql://$dbHost:$dbPort/$dbName",
            user = dbUser,
            password = dbPass
        )
    }

    override fun disconnect() {
        TODO("Is this needed ?")
    }

}