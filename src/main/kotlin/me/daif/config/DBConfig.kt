package me.daif.config

data class DatabaseConfig(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUser: String,
    val dbPass: String,
    val maxPoolSize: Int,
)
