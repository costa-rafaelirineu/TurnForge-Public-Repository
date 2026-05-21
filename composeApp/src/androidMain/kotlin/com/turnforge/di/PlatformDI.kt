package com.turnforge.di

import com.turnforge.viewmodel.AndroidTurnViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModelOf

actual val platformModule: Module = module {
    viewModelOf(::AndroidTurnViewModel)
}
