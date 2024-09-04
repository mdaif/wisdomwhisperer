package me.daif.di

import me.daif.config.AppConfig
import me.daif.database.DatabaseFactory
import me.daif.database.DatabaseFactoryImp
import me.daif.features.profile.domain.repository.ProfileRepository
import me.daif.features.profile.domain.repository.ProfileRepositoryImp
import org.koin.dsl.module
import org.koin.dsl.single


val appModule = module {
    single<AppConfig> { AppConfig() }
    single<DatabaseFactory> { DatabaseFactoryImp(get()) }
    single<ProfileRepository> { ProfileRepositoryImp()}
}
