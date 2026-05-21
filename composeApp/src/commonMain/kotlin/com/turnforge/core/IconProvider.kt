package com.turnforge.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun appIconPainter(name: String = "attack_icon"): Painter?
