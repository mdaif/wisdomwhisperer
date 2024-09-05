package me.daif.testutils

import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin


fun configuredTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = run {
    testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        startKoin {
            modules(testModule)
        }
        block()
    }
}
