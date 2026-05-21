package com.turnforge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turnforge.ui.theme.Blue
import com.turnforge.ui.theme.Green
import com.turnforge.ui.theme.Primary
import com.turnforge.ui.theme.PrimaryLight
import com.turnforge.ui.theme.Yellow

@Composable
fun ActionGrid(
    onAttack: () -> Unit = {},
    onMagic: () -> Unit = {},
    onDice: () -> Unit = {},
    onDefend: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // maxWidth here is the content width after padding
        val spacing = 8.dp
        // ActionButton now uses a single Box(size) and optional border. Calculate size so
        // two buttons plus the spacing exactly fill the available width.
        val buttonSize = (maxWidth - spacing) / 2
        val minSize = 56.dp
        val finalButtonSize = if (buttonSize < minSize) minSize else buttonSize

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionButton(
                    icon = "⚔️",
                    label = "Ataque",
                    iconName = "attack_icon",
                    onClick = onAttack,
                    glow = listOf(Primary, PrimaryLight),
                    useAppIcon = true,
                    width = finalButtonSize,
                    height = finalButtonSize * 0.855f
                )
                ActionButton(
                    icon = "✨",
                    label = "Magia",
                    iconName = "life_icon",
                    onClick = onMagic,
                    glow = listOf(Blue, PrimaryLight),
                    useAppIcon = true,
                    width = finalButtonSize,
                    height = finalButtonSize * 0.855f
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionButton(
                    icon = "🎲",
                    label = "Dados",
                    iconName = "dice_image",
                    onClick = onDice,
                    glow = listOf(Yellow, PrimaryLight),
                    useAppIcon = true,
                    width = finalButtonSize,
                    height = finalButtonSize * 0.855f
                )
                ActionButton(
                    icon = "🛡️",
                    label = "Defesa",
                    iconName = "deffense_icon",
                    onClick = onDefend,
                    glow = listOf(Green, PrimaryLight),
                    useAppIcon = true,
                    width = finalButtonSize,
                    height = finalButtonSize * 0.855f
                )
            }
        }
    }
}

