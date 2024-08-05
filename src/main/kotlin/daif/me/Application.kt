package daif.me

import daif.me.plugins.configureRouting
import daif.me.plugins.configureSecurity
import daif.me.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting()
}
