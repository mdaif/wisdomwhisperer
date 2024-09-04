package me.daif.config

data class DatabaseConfig(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUser: String,
    val dbPass: String,

    // Hikari
    val dbPoolName: String,
    val dbDriverClassName: String,
    val maxPoolSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val connectionTimeout: Long,
    val maxLifetime: Long,
)
