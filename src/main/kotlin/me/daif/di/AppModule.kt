package me.daif.di

import me.daif.config.AppConfig
import me.daif.database.DatabaseFactory
import me.daif.database.DatabaseFactoryImp
import me.daif.features.auth.jwkprovider.AwsCognitoVerifier
import me.daif.features.auth.jwkprovider.JwtVerifier
import me.daif.features.profile.domain.repository.ProfileRepository
import me.daif.features.profile.domain.repository.ProfileRepositoryImp
import org.koin.dsl.module


val appModule = module {
    single<AppConfig> { AppConfig() }
    single<DatabaseFactory> { DatabaseFactoryImp(get()) }
    single<ProfileRepository> { ProfileRepositoryImp()}
    single<JwtVerifier> { AwsCognitoVerifier() }
}
