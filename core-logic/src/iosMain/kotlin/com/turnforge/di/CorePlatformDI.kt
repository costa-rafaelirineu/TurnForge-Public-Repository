package com.turnforge.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val corePlatformModule: Module = module {
    // Implementação iOS do banco de dados (ex: Room KMP ou SQLDelight) viria aqui
}
