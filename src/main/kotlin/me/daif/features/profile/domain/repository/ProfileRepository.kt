package me.daif.features.profile.domain.repository

import me.daif.features.profile.domain.model.ProfileDTO


interface ProfileRepository {
    suspend fun profileByEmail(email: String): ProfileDTO?
    suspend fun addProfile(profile: ProfileDTO)
    suspend fun removeProfileByEmail(email: String): Boolean
}
