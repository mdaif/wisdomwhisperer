
val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val cognito_provider_version: String by project
val exposed_version: String by project


buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.7.2")
        classpath("org.flywaydb:flyway-database-postgresql:10.17.0")
    }
}

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("org.flywaydb.flyway") version "10.17.0"
}

group = "daif.me"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")
    implementation("org.flywaydb:flyway-core:10.17.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-database-postgresql:10.17.0")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}


val dbPort: String = project.findProperty("DB_PORT").toString()
val dbHost: String = project.findProperty("DB_HOST").toString()
val dbName: String = project.findProperty("DB_NAME").toString()
val dbUser: String = project.findProperty("DB_USER").toString()
val dbPASS: String = project.findProperty("DB_PASS").toString()

flyway {
    url = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    user = dbUser
    password = dbPASS
    schemas = arrayOf("public")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
