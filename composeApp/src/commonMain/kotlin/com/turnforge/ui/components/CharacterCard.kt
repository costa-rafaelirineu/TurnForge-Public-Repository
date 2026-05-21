package com.turnforge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.core.appIconPainter
import com.turnforge.ui.theme.HpGreen
import com.turnforge.ui.theme.ManaBlue
import com.turnforge.ui.theme.Surface

@Composable
fun CharacterCard(
    name: String,
    image: String = "default_profile",
    level: String,
    race: String,
    clazz: String,
    currentHp: Int,
    maxHp: Int,
    currentMana: Int,
    maxMana: Int,
    isInfiniteMana: Boolean = false,
    onClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "cardScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(if (isPressed) Surface.copy(alpha = 0.8f) else Surface)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Avatar Box
            Box(
                modifier = Modifier
                    .size(123.5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF252932)),
                contentAlignment = Alignment.Center
            ) {
                val p: Painter? = appIconPainter(image)
                if (p != null) {
                    Image(
                        painter = p,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("?", color = Color.White.copy(alpha = 0.3f), fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info and Bars
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Lvl $level • $race • $clazz".uppercase(),
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // HP Bar
                StatBar(
                    label = "PONTOS DE VIDA",
                    current = currentHp,
                    max = maxHp,
                    colors = listOf(HpGreen, Color(0xFF2ECC71))
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Mana Bar
                StatBar(
                    label = "PONTOS DE MANA",
                    current = currentMana,
                    max = maxMana,
                    colors = listOf(ManaBlue, Color(0xFF3498DB)),
                    isInfinite = isInfiniteMana
                )
            }
        }
    }
}
