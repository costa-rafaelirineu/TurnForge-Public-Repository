package com.turnforge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.core.appIconPainter
import com.turnforge.ui.theme.Secondary

@Composable
fun ActionButton(
    icon: String? = null,
    painter: Painter? = null,
    iconName: String? = null,
    label: String,
    modifier: Modifier = Modifier,
    glow: List<Color>? = null,
    width: Dp = 120.dp,
    height: Dp = 120.dp,
    useAppIcon: Boolean = false,
    onClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "buttonScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    val shape = RoundedCornerShape(16.dp)
    val resolvedPainter: Painter? = if (useAppIcon) {
        appIconPainter(iconName ?: "attack_icon")
    } else {
        painter
    }

    // Glass effect colors
    val accentColor = glow?.firstOrNull() ?: Color.White
    val backgroundColor =
        if (isPressed) accentColor.copy(alpha = 0.15f) else accentColor.copy(alpha = 0.08f)
    val borderColor =
        if (isPressed) accentColor.copy(alpha = 0.5f) else accentColor.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(Secondary.copy(alpha = 0.4f)) // Dark glass base
            .background(backgroundColor) // Tint
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (resolvedPainter != null) {
                Image(
                    painter = resolvedPainter,
                    contentDescription = label,
                    modifier = Modifier.size(100.dp) // Reduzido para caber melhor na nova altura
                )
            } else if (icon != null) {
                Text(icon, fontSize = 40.sp)
            }

            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}
