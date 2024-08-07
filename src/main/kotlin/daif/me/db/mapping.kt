package daif.me.db

import daif.me.model.Profile
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object ProfileTable : IntIdTable("profile") {
    val email = varchar("email", 50)
    val phone = varchar("phone", 50)
}

class ProfileDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProfileDAO>(ProfileTable)

    var email by ProfileTable.email
    var phone by ProfileTable.phone
}


suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)


fun daoToModel(dao: ProfileDAO) = Profile(
    dao.email,
    dao.phone,
)
