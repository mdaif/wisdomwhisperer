package me.daif.whatsapp

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.daif.features.profile.domain.model.ProfileDTO


@Serializable
data class WhatsAppMessage(
    val messaging_product: String,
    val to: String,
    val type: String,
    val text: Text
)

@Serializable
data class Text(
    val body: String
)


class WhatsappCommunication : CommunicationInterface {
    override suspend fun sendMessageByPhone(recipientPhoneNumber: String, message: String) {
        val graphApiAccessToken = System.getenv("GRAPH_API_ACCESS_TOKEN")
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            defaultRequest {
                header("Authorization", "Bearer $graphApiAccessToken")
            }
        }
        val senderPhoneNumber = System.getenv("SENDER_PHONE_ID")
        val graphApiUrl = "https://graph.facebook.com/v20.0/$senderPhoneNumber/messages"

        val response: HttpResponse = client.post(graphApiUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                WhatsAppMessage(
                    messaging_product = "whatsapp",
                    to = recipientPhoneNumber,
                    type = "text",
                    text = Text(
                        body = message
                    )
                )
            )
        }
        println(response.bodyAsText())
    }

    override suspend fun sendMessage(profile: ProfileDTO, message: String) {
        val recipientPhoneNumber = profile.phone
        sendMessageByPhone(recipientPhoneNumber, message)

    }

}