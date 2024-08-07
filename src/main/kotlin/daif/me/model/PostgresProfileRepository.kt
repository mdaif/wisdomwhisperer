package daif.me.model

import daif.me.db.ProfileDAO
import daif.me.db.ProfileTable
import daif.me.db.daoToModel
import daif.me.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PostgresProfileRepository: ProfileRepository {
    override suspend fun profileByEmail(email: String): Profile? = suspendTransaction {
        ProfileDAO
            .find { (ProfileTable.email eq email)}
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addProfile(profile: Profile): Unit = suspendTransaction {
        ProfileDAO.new {
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