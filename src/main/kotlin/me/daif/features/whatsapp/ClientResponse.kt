package me.daif.features.whatsapp

import me.daif.plugins.MetaWebhookPayload
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }


suspend fun processClientResponse(rawBody: String) {
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
}