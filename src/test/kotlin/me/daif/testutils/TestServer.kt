package me.daif.testutils

import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.module.Module


fun configuredTestApplication(kotlinModules: List<Module> = listOf(testModule), block: suspend ApplicationTestBuilder.() -> Unit) = run {
    testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        startKoin {
            modules(kotlinModules)
        }
        block()
    }
}
