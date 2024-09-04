package me.daif.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val email: String,
    val phone: String,
)