package daif.me.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database


fun Application.configureDatabases() {
    val dbPort: String = System.getenv("DB_PORT")
    val dbHost: String = System.getenv("DB_HOST")
    val dbName: String = System.getenv("DB_NAME")
    val dbUser: String = System.getenv("DB_USER")
    val dbPASS: String = System.getenv("DB_PASS")

    Database.connect(
        "jdbc:postgresql://$dbHost:$dbPort/$dbName",
        user = dbUser,
        password = dbPASS
    )
}
