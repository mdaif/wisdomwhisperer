package me.daif.features.profile.domain.repository

import me.daif.database.suspendTransaction
import me.daif.features.profile.data.dao.Profile
import me.daif.features.profile.data.dao.ProfileTable
import me.daif.features.profile.domain.mapper.toDTO
import me.daif.features.profile.domain.model.ProfileDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class ProfileRepositoryImp : ProfileRepository {
    override suspend fun profileByEmail(email: String): ProfileDTO? = suspendTransaction {
        val profilesMatchingEmail = Profile.find { (ProfileTable.email eq email) }
        Profile
            .find { (ProfileTable.email eq email) }
            .limit(1)
            .firstOrNull()?.toDTO()
    }

    override suspend fun addProfile(profile: ProfileDTO): Unit = suspendTransaction {
        Profile.new {
            email = profile.email
            phone = profile.phone
        }
    }

    override suspend fun removeProfileByEmail(email: String): Boolean = suspendTransaction {
        val rowsDeleted = ProfileTable.deleteWhere {
            ProfileTable.email eq email
        }
        rowsDeleted == 1
    }

}