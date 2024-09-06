package me.daif.testutils.profile.domain.repository

import me.daif.features.profile.domain.model.ProfileDTO
import me.daif.features.profile.domain.repository.ProfileRepository

class TestProfileRepository: ProfileRepository {
    /*
    * Will be used to make tests that don't require actual
    * profiles to pass. For example, the authentication tests.
    * */
    override suspend fun profileByEmail(email: String): ProfileDTO = ProfileDTO(email=email, phone = "")
    override suspend fun addProfile(profile: ProfileDTO) {}
    override suspend fun removeProfileByEmail(email: String) = true
}