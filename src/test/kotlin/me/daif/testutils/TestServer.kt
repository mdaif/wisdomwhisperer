package me.daif.testutils

import io.ktor.server.config.*
import io.ktor.server.testing.*


fun configuredTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = run {
    testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        block()
    }
}
