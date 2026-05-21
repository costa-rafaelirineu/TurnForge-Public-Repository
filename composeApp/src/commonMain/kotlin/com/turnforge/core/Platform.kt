package com.turnforge.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform