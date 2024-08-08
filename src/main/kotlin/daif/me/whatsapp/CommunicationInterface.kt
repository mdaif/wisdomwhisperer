package daif.me.whatsapp

import daif.me.model.Profile


interface CommunicationInterface {
    suspend fun sendMessage(profile: Profile, message: String)
}