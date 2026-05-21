package com.turnforge

import android.app.Application
import com.turnforge.core.ContextProvider
import com.turnforge.di.commonModule
import com.turnforge.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TurnForgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)
        
        startKoin {
            androidLogger()
            androidContext(this@TurnForgeApplication)
            modules(commonModule, platformModule)
        }
    }
}
