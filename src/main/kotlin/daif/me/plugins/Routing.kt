package daif.me.plugins

import daif.me.auth.buildAuthUrl
import daif.me.auth.exchangeCodeForToken
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


private val json = Json { ignoreUnknownKeys = true }

fun Application.configureRouting(profileRepository: ProfileRepository) {
    routing {

        staticResources("static", "static")

        get("/login") {
            val authUrl = buildAuthUrl()
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
            val payload = json.decodeFromString<MetaWebhookPayload>(rawBody)

            val whatsappCommunication = WhatsappCommunication()

            if (payload.objectType == "whatsapp_business_account") {
                payload.entry.forEach { entry ->
                    entry.changes.forEach { change ->
                        if (change.field == "messages") {
                            change.value.contacts?.forEach { contact ->
                                val clientNumber = contact.waId
                                change.value.messages?.forEach { message ->
                                    if (message.type == "text") {
                                        val messageText = message.text.body
                                        whatsappCommunication.sendMessageByPhone(
                                            clientNumber,
                                            "You've just sent me:\n\n $messageText",
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
            // We need to acknowledge to the server, so it doesn't keep sending
            // the same message.
            call.respond(HttpStatusCode.OK)
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


@Serializable
data class MetaWebhookPayload(
    @SerialName("object") val objectType: String,
    val entry: List<WhatsappEntry>
)

@Serializable
data class WhatsappEntry(
    val id: String,
    val changes: List<WhatsappChange>
)

@Serializable
data class WhatsappChange(
    val field: String,
    val value: WhatsappValue,
)

@Serializable
data class WhatsappValue(
    @SerialName("messaging_product") val messagingProduct: String,
    val metadata: WhatsappMetadata,
    val contacts: List<ContactItem>? = null,
    val messages: List<MessageItem>? = null,
)

@Serializable
data class WhatsappMetadata(
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
