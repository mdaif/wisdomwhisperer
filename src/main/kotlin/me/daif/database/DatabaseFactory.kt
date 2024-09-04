package me.daif.database

interface DatabaseFactory {
    fun connect()
    fun disconnect()
}