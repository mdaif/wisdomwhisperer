package daif.me.whatsapp

import daif.me.model.Profile


interface CommunicationInterface {
    suspend fun sendMessageByPhone(recipientPhoneNumber: String, message: String)
    suspend fun sendMessage(profile: Profile, message: String)
}