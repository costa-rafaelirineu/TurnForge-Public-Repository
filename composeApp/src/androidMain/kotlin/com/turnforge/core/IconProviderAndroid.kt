package com.turnforge.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter

@Composable
actual fun appIconPainter(name: String): Painter? {
    if (name.startsWith("content://") || name.startsWith("file://") || name.startsWith("http")) {
        return rememberAsyncImagePainter(name)
    }
    // Check resource availability via context first to avoid errors in previews.
    val ctx = LocalContext.current
    val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
    return if (resId != 0) painterResource(id = resId) else null
}


