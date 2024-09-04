package me.daif.features.profile.data.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ProfileTable : IntIdTable("profile") {
    val email = varchar("email", 50)
    val phone = varchar("phone", 50)
}

class Profile(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Profile>(ProfileTable)

    var email by ProfileTable.email
    var phone by ProfileTable.phone
}
