package me.daif

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.daif.testutils.configuredTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import org.koin.test.AutoCloseKoinTest
import me.daif.features.auth.jwkprovider.createJwtToken
import me.daif.features.profile.domain.repository.ProfileRepository
import me.daif.testutils.profile.domain.repository.TestProfileRepository
import me.daif.testutils.testModule
import org.koin.dsl.module


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
    fun testAuthenticationWorks() = configuredTestApplication(
        testModule.plus(
            module {
                single<ProfileRepository> { TestProfileRepository() }
            }
        )
    ) {
        val username = "steve@example.com"
        val token = createJwtToken(username)
        client.get("/profile/byEmail/$username") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.apply {

            assertEquals(HttpStatusCode.OK, status, "Failed!: ${body() as String}")
        }
    }
}
