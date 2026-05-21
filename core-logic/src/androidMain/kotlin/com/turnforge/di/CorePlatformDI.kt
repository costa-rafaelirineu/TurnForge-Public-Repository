package com.turnforge.di

import com.turnforge.local.AppDatabase
import com.turnforge.repository.RoomTurnRepository
import com.turnforge.repository.TurnRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val corePlatformModule: Module = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().turnDao() }
    single { get<AppDatabase>().archivedHistoryDao() }
    
    singleOf(::RoomTurnRepository) bind TurnRepository::class
}
