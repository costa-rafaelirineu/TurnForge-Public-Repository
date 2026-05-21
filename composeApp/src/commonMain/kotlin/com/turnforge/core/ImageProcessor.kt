package com.turnforge.core

import androidx.compose.ui.geometry.Rect

expect suspend fun cropImage(uri: String, cropRect: Rect): String
