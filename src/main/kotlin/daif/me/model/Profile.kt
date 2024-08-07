package daif.me.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val email: String,
    val phone: String,
)