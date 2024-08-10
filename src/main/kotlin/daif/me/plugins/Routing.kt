package daif.me.plugins

import daif.me.model.Profile
import daif.me.model.ProfileRepository
import daif.me.whatsapp.WhatsappCommunication
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


private val json = Json { ignoreUnknownKeys = true }

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

        /**
         * This endpoint receives an authentication challenge from the
         * Fb graph api.
        * */
        get("/facebook-webhook") {

            val challenge = call.parameters["hub.challenge"]
            val receivedVerificationToken = call.parameters["hub.verify_token"]

            val originalVerificationToken = System.getenv("VERIFICATION_TOKEN")
            if (receivedVerificationToken == originalVerificationToken) {
                call.respondText(challenge ?: "", status = HttpStatusCode.OK)
            }
        }

        /*
        * Receive the actual webhook notification
        * */
        post("/facebook-webhook") {
            val rawBody = call.receiveText()
            val jsonObject = json.parseToJsonElement(rawBody).jsonObject
            when {
                jsonObject["object"]?.jsonPrimitive?.content == "whatsapp_business_account" -> {
                    val message = json.decodeFromString<FacebookWebhookMessage>(rawBody)
                    val clientNumber = message.entry.first().changes.first().value.contacts?.first()?.waId
                    val messageText = message.entry.first().changes.first().value.messages?.first()?.text?.body
                    val whatsappCommunication = WhatsappCommunication()
                    println("############")
                    println("############")
                    println(rawBody)
                    println("############")
                    println("############")
                    if (clientNumber != null && messageText != null)
                        whatsappCommunication.sendMessageByPhone(clientNumber, "You've just sent me:\n\n $messageText")

                }
            }

        }

        authenticate("auth-jwt") {
            route("/profile") {
                post("") {
                    val profile = call.receive<Profile>()
                    profileRepository.addProfile(profile)
                    call.respond(HttpStatusCode.Created)
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

                post("/byEmail/{email}/notifications") {
                    val email = call.parameters["email"]
                    val message = call.request.queryParameters["message"]
                    if (email == null || message == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val profile = profileRepository.profileByEmail(email)
                    if (profile == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@post
                    }
                    val whatsappCommunication = WhatsappCommunication()
                    whatsappCommunication.sendMessage(profile, message)
                    call.respond("Sent message to ${profile.phone}")
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


@Serializable
data class FacebookWebhookMessage(
    @SerialName("object") val objectType: String,
    val entry: List<FacebookMessageEntry>
)

@Serializable
data class FacebookMessageEntry(
    val id: String,
    val changes: List<FacebookMessageChange>
)

@Serializable
data class FacebookMessageChange(
    val field: String,
    val value: FacebookMessageValue,
)

@Serializable
data class FacebookMessageValue(
    @SerialName("messaging_product") val messagingProduct: String,
    val metadata: FacebookMetadata,
    val contacts: List<ContactItem>? = null,
    val messages: List<MessageItem>? = null,
)

@Serializable
data class FacebookMetadata(
    @SerialName("display_phone_number") val displayPhoneNumber: String,
    @SerialName("phone_number_id") val phoneNumberId: String,
)

@Serializable
data class ContactItem(
    val profile: ProfileItem,
    @SerialName("wa_id") val waId: String
)

@Serializable
data class ProfileItem(
    val name: String,
)


@Serializable
data class MessageItem(
    val from: String,
    val id: String,
    val timestamp: String,
    val type: String,
    val text: TextBody,
)

@Serializable
data class TextBody(
    val body: String,
)