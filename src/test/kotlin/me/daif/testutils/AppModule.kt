package me.daif.testutils

import me.daif.config.AppConfig
import me.daif.database.DatabaseFactory
import me.daif.database.DatabaseFactoryImp
import me.daif.features.auth.jwkprovider.JwkProvider
import me.daif.features.auth.jwkprovider.TestJwkProvider
import me.daif.features.profile.domain.repository.ProfileRepository
import me.daif.features.profile.domain.repository.ProfileRepositoryImp
import org.koin.dsl.module

val testModule = module {
    single<AppConfig> { AppConfig() }
    single<DatabaseFactory> { DatabaseFactoryImp(get()) }
    single<ProfileRepository> { ProfileRepositoryImp() }
    single<JwkProvider> { TestJwkProvider() }
}