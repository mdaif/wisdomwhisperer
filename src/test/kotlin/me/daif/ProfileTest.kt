package me.daif

import io.ktor.client.request.*
import io.ktor.http.*
import me.daif.testutils.configuredTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import org.koin.test.AutoCloseKoinTest


class ProfileTest: AutoCloseKoinTest() {

    @Test
    fun testGetProfileByEmailUnauthenticated() = configuredTestApplication {
        // TODO: create an actual profile before running the test, even though we know
        // the 403 is returned before hitting the application logic.
        client.get("/profile/byEmail/existingUser@example.com").apply {
            assertEquals(HttpStatusCode.Forbidden, status)
        }
    }

    @Test
    fun testAuthenticationWorks() = configuredTestApplication {

    }
}
