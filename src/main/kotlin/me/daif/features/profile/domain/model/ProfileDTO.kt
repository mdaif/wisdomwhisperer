package me.daif.features.profile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val email: String,
    val phone: String,
)
