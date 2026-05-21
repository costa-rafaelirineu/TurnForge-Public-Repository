package com.turnforge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.ui.theme.Red
import com.turnforge.ui.theme.Surface

@Composable
fun EnemyCard(
    name: String,
    currentHp: Int,
    maxHp: Int,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    isSelected: Boolean = false,
    onDelete: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Feedback visual de escala (encolhe ao tocar)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "cardScale"
    )

    // Vibração imediata ao encostar o dedo
    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    val targetProgress = if (maxHp > 0) (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500)
    )

    val borderColor =
        if (isSelected) Red else if (isPressed) Red.copy(alpha = 0.5f) else Color.Transparent

    Box(
        modifier = modifier
            .width(140.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isPressed) Surface.copy(alpha = 0.8f) else Surface.copy(alpha = 0.5f))
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Red)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = name.uppercase(),
                    color = Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currentHp HP",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/ $maxHp",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // HP Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(Red)
                    )
                }
            }
        }

        if (onDelete != null) {
            val actionInteractionSource = remember { MutableInteractionSource() }
            val isActionPressed by actionInteractionSource.collectIsPressedAsState()
            val actionScale by animateFloatAsState(if (isActionPressed) 0.85f else 1f)

            LaunchedEffect(isActionPressed) {
                if (isActionPressed) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = actionScale
                        scaleY = actionScale
                    }
                    .background(
                        if (isEditing) MaterialTheme.colorScheme.error else Color.White.copy(alpha = 0.1f),
                        androidx.compose.foundation.shape.CircleShape
                    )
                    .border(
                        1.dp,
                        if (isEditing) Color.Transparent else Color.White.copy(alpha = 0.2f),
                        androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable(
                        interactionSource = actionInteractionSource,
                        indication = ripple(bounded = false, radius = 12.dp),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (isEditing) onDelete.invoke() else onActionClick()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "⚔️",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
