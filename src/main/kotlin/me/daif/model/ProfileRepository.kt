package me.daif.model


interface ProfileRepository {
    suspend fun profileByEmail(email: String): Profile?
    suspend fun addProfile(profile: Profile)
    suspend fun removeProfileByEmail(email: String): Boolean
}
