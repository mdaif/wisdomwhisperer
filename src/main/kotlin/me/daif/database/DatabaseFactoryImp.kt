package me.daif.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.daif.config.AppConfig
import org.jetbrains.exposed.sql.Database


class DatabaseFactoryImp(appConfig: AppConfig): DatabaseFactory {
    // appConfig is provided via Koin DI magic.
    private val dbConfig = appConfig.dbConfig

    override fun connect() {
        val dataSource = createHikariDataSource()
        Database.connect(dataSource)
    }

    private fun createHikariDataSource(): HikariDataSource {
        val dbHost: String = dbConfig.dbHost
        val dbPort: String = dbConfig.dbPort
        val dbName: String = dbConfig.dbName

        val hikariConfig = HikariConfig().apply {
            poolName = dbConfig.dbPoolName
            driverClassName = dbConfig.dbDriverClassName
            jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
            username = dbConfig.dbUser
            password = dbConfig.dbPass
            maximumPoolSize = dbConfig.maxPoolSize
            minimumIdle = dbConfig.minimumIdle
            idleTimeout = dbConfig.idleTimeout
            connectionTimeout = dbConfig.connectionTimeout
            maxLifetime = dbConfig.maxLifetime
        }
        return HikariDataSource(hikariConfig)
    }

    override fun disconnect() {
        TODO("Is this needed ?")
    }

}