package com.turnforge.ui.components

import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.model.combat.ActiveStatus
import com.turnforge.model.combat.StatusEffectType
import com.turnforge.ui.theme.Green
import com.turnforge.ui.theme.Poison
import com.turnforge.ui.theme.Purple
import com.turnforge.ui.theme.Red

@Composable
fun StatusRow(
    activeStatuses: List<ActiveStatus> = emptyList(),
    defenseLabels: List<String> = emptyList(),
    isEditing: Boolean = false,
    onDeleteStatus: (String) -> Unit = {}
) {
    val displayItems = mutableListOf<StatusDisplayItem>()

    activeStatuses.forEach { status ->
        val icon = when (status.type) {
            StatusEffectType.BUFF_AC -> "🛡️"
            StatusEffectType.DEBUFF_ATTACK -> "⚔️↓"
            StatusEffectType.DOT_DAMAGE -> "☠️"
            StatusEffectType.HOT_HEAL -> "💖"
            StatusEffectType.REDUCE_DAMAGE -> status.icon
            StatusEffectType.CUSTOM -> status.icon
        }

        val color = when (status.type) {
            StatusEffectType.BUFF_AC -> Green
            StatusEffectType.DEBUFF_ATTACK -> Red
            StatusEffectType.DOT_DAMAGE -> Poison
            StatusEffectType.HOT_HEAL -> Green
            StatusEffectType.REDUCE_DAMAGE -> Green
            StatusEffectType.CUSTOM -> Purple
        }

        val label = "$icon ${status.name} (${status.remainingDuration})"
        displayItems.add(StatusDisplayItem(status.id, label, color, isDeletable = true))
    }


    defenseLabels.forEachIndexed { index, label ->
        displayItems.add(
            0,
            StatusDisplayItem("defending_$index", label, Green, isDeletable = false)
        )
    }

    val haptic = LocalHapticFeedback.current

    if (displayItems.isEmpty()) {
        Text(
            "NENHUM EFEITO ATIVO",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(
                items = displayItems,
                key = { it.id }
            ) { item ->
                Box {
                    StatusChip(item.label, item.color)

                    if (isEditing && item.isDeletable) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(if (isPressed) 0.8f else 1f)

                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(16.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(bounded = false, radius = 8.dp),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onDeleteStatus(item.id)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remover",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class StatusDisplayItem(
    val id: String,
    val label: String,
    val color: androidx.compose.ui.graphics.Color,
    val isDeletable: Boolean
)
