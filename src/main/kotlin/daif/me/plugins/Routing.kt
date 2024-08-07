package daif.me.plugins

import daif.me.model.Profile
import daif.me.model.ProfileRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


fun Application.configureRouting(profileRepository: ProfileRepository) {
    routing {

        staticResources("static", "static")

        get("/login") {
            val clientId = System.getenv("COGNITO_CLIENT_ID")
            val redirectUri = System.getenv("AUTH_REDIRECT_URL")
            val region = System.getenv("AWS_REGION")
            val appName = System.getenv("APP_NAME")
            val authDomain = "https://$appName.auth.$region.amazoncognito.com/oauth2/authorize"
            val authUrl = URLBuilder(authDomain).apply {
                parameters.append("client_id", clientId)
                parameters.append("response_type", "code")
                parameters.append("scope", "openid")
                parameters.append("redirect_uri", redirectUri)
            }.buildString()

            call.respondRedirect(authUrl)
        }

        get("/cognito-auth-callback") {
            val code = call.parameters["code"]
            if (code == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }

            val token = exchangeCodeForToken(code)
            call.respond(mapOf("token" to token))
        }

        authenticate("auth-jwt") {
            route("/profile") {
                post("") {
                    val profile = call.receive<Profile>()
                    profileRepository.addProfile(profile)
                    call.respond(HttpStatusCode.Created)
                }
                put("/byEmail/{email}") {

                }
                get("/byEmail/{email}") {
                    val email = call.parameters["email"]
                    if (email == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val profile = profileRepository.profileByEmail(email)
                    if (profile == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(profile)
                }
            }

        }

    }
}

fun exchangeCodeForToken(code: String): TokenResponse? {
    val clientId = System.getenv("COGNITO_CLIENT_ID")
    val clientSecret = System.getenv("COGNITO_CLIENT_SECRET")
    val redirectUri = System.getenv("AUTH_REDIRECT_URL")
    val region = System.getenv("AWS_REGION")
    val appName = System.getenv("APP_NAME")
    val cognitoDomain = "$appName.auth.$region.amazoncognito.com"
    val tokenUrl = "https://$cognitoDomain/oauth2/token"

    val client = OkHttpClient()

    val formBody = FormBody.Builder()
        .add("grant_type", "authorization_code")
        .add("client_id", clientId)
        .add("client_secret", clientSecret)
        .add("redirect_uri", redirectUri)
        .add("code", code)
        .build()

    val request = Request.Builder()
        .url(tokenUrl)
        .post(formBody)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Accept", "application/json")
        .build()
    try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IllegalStateException("Unexpected code $response")

        val responseBody = response.body?.string()
        if (responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            return TokenResponse(
                accessToken = jsonObject.getString("access_token"),
                idToken = jsonObject.getString("id_token"),
                refreshToken = jsonObject.getString("refresh_token"),
                expiresIn = jsonObject.getInt("expires_in"),
                tokenType = jsonObject.getString("token_type")
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

@Serializable
data class TokenResponse(
    val accessToken: String,
    val idToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val tokenType: String
)
