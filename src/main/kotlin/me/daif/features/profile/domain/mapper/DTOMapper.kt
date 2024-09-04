package me.daif.features.profile.domain.mapper

import me.daif.features.profile.data.dao.Profile
import me.daif.features.profile.domain.model.ProfileDTO


fun Profile.toDTO() = ProfileDTO(email=this.email, phone=this.phone)
