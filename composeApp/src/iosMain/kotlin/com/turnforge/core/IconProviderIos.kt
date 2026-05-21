package com.turnforge.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * iOS actual for appIconPainter — returns null (no default drawable on iOS yet).
 * This satisfies the expect/actual requirement for iOS targets.
 */
@Composable
actual fun appIconPainter(name: String): Painter? = null
