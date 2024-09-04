package me.daif.di

import me.daif.config.AppConfig
import org.koin.dsl.module
import org.koin.dsl.single


val appModule = module {
    single<AppConfig> { AppConfig() }
}