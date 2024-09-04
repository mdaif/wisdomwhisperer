package me.daif.whatsapp

import me.daif.features.profile.domain.model.ProfileDTO


interface CommunicationInterface {
    suspend fun sendMessageByPhone(recipientPhoneNumber: String, message: String)
    suspend fun sendMessage(profile: ProfileDTO, message: String)
}