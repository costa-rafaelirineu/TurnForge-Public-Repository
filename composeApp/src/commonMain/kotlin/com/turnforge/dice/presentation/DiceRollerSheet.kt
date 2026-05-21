package com.turnforge.dice.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.dice.domain.model.RollMode
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.aguardando_rolagem
import turnforge.composeapp.generated.resources.bonus_field_title
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.dado_label
import turnforge.composeapp.generated.resources.dados_label
import turnforge.composeapp.generated.resources.desvantagem_label
import turnforge.composeapp.generated.resources.expressao_custom_label
import turnforge.composeapp.generated.resources.expressao_placeholder
import turnforge.composeapp.generated.resources.finish_button
import turnforge.composeapp.generated.resources.modo_rolagem_label
import turnforge.composeapp.generated.resources.dados_generic_label
import turnforge.composeapp.generated.resources.multi_dice_mode_label
import turnforge.composeapp.generated.resources.normal_label
import turnforge.composeapp.generated.resources.quantidade_label
import turnforge.composeapp.generated.resources.rolagem_dados_titulo
import turnforge.composeapp.generated.resources.rolar_dados_button
import turnforge.composeapp.generated.resources.soma_label
import turnforge.composeapp.generated.resources.vantagem_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerSheet(
    viewModel: DiceViewModel,
    onDismiss: () -> Unit
) {
    val lastResult by viewModel.lastResult.collectAsState()
    val availableDice = listOf(20, 12, 10, 8, 6, 4)
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.rolagem_dados_titulo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 0. Expressão Customizada
            Text(
                stringResource(Res.string.expressao_custom_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = viewModel.expression,
                onValueChange = { viewModel.expression = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(Res.string.expressao_placeholder),
                        color = Color.White.copy(alpha = 0.2f),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    cursorColor = Color(0xFF3B82F6)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Checkbox para modo de múltiplos dados
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.isMultiDiceMode,
                    onCheckedChange = { viewModel.isMultiDiceMode = it },
                    modifier = Modifier.offset(x = (-12).dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF3B82F6),
                        uncheckedColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Text(
                    stringResource(Res.string.multi_dice_mode_label),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.offset(x = (-12).dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Seleção de Dados
            Text(
                stringResource(Res.string.dados_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (viewModel.isMultiDiceMode) {
                // Interface para múltiplos dados
                Column(modifier = Modifier.fillMaxWidth()) {
                    availableDice.forEach { sides ->
                        val currentQuantity = viewModel.multiDiceSelections[sides] ?: 0
                        MultiDiceRow(
                            diceLabel = "d$sides",
                            quantity = currentQuantity,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateMultiDiceSelection(sides, newQuantity)
                            }
                        )
                        if (sides != availableDice.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            } else {
                // Interface original para único dado
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    items(availableDice) { sides ->
                        DiceChip(
                            label = "d$sides",
                            isSelected = viewModel.selectedSides == sides && !viewModel.isDecimal,
                            onClick = {
                                viewModel.selectedSides = sides
                                viewModel.isDecimal = false
                            }
                        )
                    }
                    item {
                        DiceChip(
                            label = "d100",
                            isSelected = viewModel.isDecimal,
                            onClick = {
                                viewModel.selectedSides = 10
                                viewModel.isDecimal = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Modo de Rolagem
            Text(
                stringResource(Res.string.modo_rolagem_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val modes = listOf(
                    Triple(
                        RollMode.DISADVANTAGE,
                        stringResource(Res.string.desvantagem_label),
                        Color(0xFFEF5350)
                    ),
                    Triple(
                        RollMode.NORMAL,
                        stringResource(Res.string.normal_label),
                        Color(0xFF3B82F6)
                    ),
                    Triple(
                        RollMode.ADVANTAGE,
                        stringResource(Res.string.vantagem_label),
                        Color(0xFF66BB6A)
                    )
                )

                modes.forEach { (mode, label, color) ->
                    val isSelected = viewModel.rollMode == mode
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f)

                    LaunchedEffect(isPressed) {
                        if (isPressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) color else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(color = color),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.rollMode = mode
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) color else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Ajustes
            Row(modifier = Modifier.fillMaxWidth()) {
                Stepper(
                    label = stringResource(Res.string.quantidade_label),
                    value = if (viewModel.isDecimal) 1 else viewModel.quantity,
                    onValueChange = { if (!viewModel.isDecimal) viewModel.quantity = it },
                    range = 1..20,
                    enabled = !viewModel.isDecimal && !viewModel.isMultiDiceMode,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Stepper(
                    label = stringResource(Res.string.bonus_field_title),
                    value = viewModel.modifier,
                    onValueChange = { viewModel.modifier = it },
                    range = -20..20,
                    isBonus = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Área de Resultado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .heightIn(min = 110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Shine layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                0.0f to Color.White.copy(alpha = 0.05f),
                                0.5f to Color.Transparent,
                                1.0f to Color.White.copy(alpha = 0.02f)
                            )
                        )
                )

                lastResult?.let { rollEvent ->
                    val result = rollEvent.result
                    val diceSum = result.rolls.sum()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (result.mode != RollMode.NORMAL && result.otherTotal != null) {
                                Text(
                                    text = result.otherTotal.toString(),
                                    fontSize = 20.sp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = result.finalTotal.toString(),
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )

                                if (result.modifier != 0 || (viewModel.isMultiDiceMode && viewModel.multiDiceSelections.size > 1)) {
                                    val sign = if (result.modifier >= 0) "+" else "-"
                                    val absMod =
                                        if (result.modifier >= 0) result.modifier else -result.modifier

                                    val text = if (result.modifier != 0) {
                                        "$diceSum $sign $absMod"
                                    } else {
                                        diceSum.toString()
                                    }

                                    Text(
                                        text = text,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Exibição detalhada para múltiplos dados
                        if (viewModel.isMultiDiceMode || result.rolls.size > 1 || result.otherRolls != null) {
                            val groupedRolls =
                                groupRollsByDiceType(result.rolls, viewModel.multiDiceSelections)
                            val discardedGrouped = result.otherRolls?.let {
                                groupRollsByDiceType(
                                    it,
                                    viewModel.multiDiceSelections
                                )
                            }

                            IndividualDiceResults(
                                groupedRolls = groupedRolls,
                                discardedGrouped = discardedGrouped
                            )
                        } else {
                            // Caso simples: apenas um dado
                            Surface(
                                color = Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    text = stringResource(
                                        Res.string.dado_label,
                                        result.rolls.first()
                                    ),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } ?: run {
                    Text(
                        stringResource(Res.string.aguardando_rolagem),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val rollInteractionSource = remember { MutableInteractionSource() }
            val isRollPressed by rollInteractionSource.collectIsPressedAsState()
            val rollScale by animateFloatAsState(if (isRollPressed) 0.95f else 1f)

            LaunchedEffect(isRollPressed) {
                if (isRollPressed) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .graphicsLayer {
                        scaleX = rollScale
                        scaleY = rollScale
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF3B82F6), Color(0xFF2DD4BF))
                        )
                    )
                    .background(if (isRollPressed) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable(
                        interactionSource = rollInteractionSource,
                        indication = ripple(color = Color.White),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.roll()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(Res.string.rolar_dados_button),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            if (lastResult != null) {
                Spacer(modifier = Modifier.height(8.dp))

                val confirmInteractionSource = remember { MutableInteractionSource() }
                val isConfirmPressed by confirmInteractionSource.collectIsPressedAsState()
                val confirmScale by animateFloatAsState(if (isConfirmPressed) 0.96f else 1f)

                LaunchedEffect(isConfirmPressed) {
                    if (isConfirmPressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .graphicsLayer {
                            scaleX = confirmScale
                            scaleY = confirmScale
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = if (isConfirmPressed) 0.12f else 0.05f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = if (isConfirmPressed) 0.2f else 0.1f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            interactionSource = confirmInteractionSource,
                            indication = ripple(color = Color.White),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDismiss()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.finish_button),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))

                val cancelInteractionSource = remember { MutableInteractionSource() }
                val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
                val cancelScale by animateFloatAsState(if (isCancelPressed) 0.96f else 1f)

                LaunchedEffect(isCancelPressed) {
                    if (isCancelPressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .graphicsLayer {
                            scaleX = cancelScale
                            scaleY = cancelScale
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = if (isCancelPressed) 0.2f else 0.1f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            interactionSource = cancelInteractionSource,
                            indication = ripple(color = Color.White),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDismiss()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.cancel_button),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IndividualDiceResults(
    groupedRolls: Map<String, List<Int>>,
    discardedGrouped: Map<String, List<Int>>? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val allKeys = (groupedRolls.keys + (discardedGrouped?.keys ?: emptySet())).distinct()
            .sortedByDescending {
                it.removePrefix("d").toIntOrNull() ?: 0
            }

        allKeys.forEach { diceType ->
            val rolls = groupedRolls[diceType] ?: emptyList()
            val discardedRolls = discardedGrouped?.get(diceType) ?: emptyList()

            if (rolls.isNotEmpty() || discardedRolls.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = diceType.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )

                        if (rolls.size > 1) {
                            Surface(
                                color = Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.soma_label, rolls.sum()),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Mostrar rolos mantidos
                        rolls.forEach { roll ->
                            DiceResultIcon(value = roll, isDiscarded = false)
                        }

                        // Mostrar rolos descartados (para vantagem/desvantagem)
                        discardedRolls.forEach { roll ->
                            DiceResultIcon(value = roll, isDiscarded = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiceResultIcon(value: Int, isDiscarded: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isDiscarded) Color.White.copy(alpha = 0.02f)
                else Color.White.copy(alpha = 0.08f)
            )
            .border(
                1.dp,
                if (isDiscarded) Color.White.copy(alpha = 0.05f)
                else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDiscarded) Color.White.copy(alpha = 0.2f) else Color.White,
            textDecoration = if (isDiscarded) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
        )
    }
}

@Composable
fun Stepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    enabled: Boolean = true,
    isBonus: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (value > range.first) onValueChange(value - 1)
                },
                enabled = enabled && value > range.first
            ) {
                Text(
                    text = "−",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (enabled && value > range.first) Color.White else Color.White.copy(
                        alpha = 0.2f
                    )
                )
            }

            Text(
                text = if (isBonus && value > 0) "+$value" else "$value",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = if (enabled) Color.White else Color.White.copy(alpha = 0.3f)
            )

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (value < range.last) onValueChange(value + 1)
                },
                enabled = enabled && value < range.last
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar",
                    modifier = Modifier.size(20.dp),
                    tint = if (enabled && value < range.last) Color.White else Color.White.copy(
                        alpha = 0.2f
                    )
                )
            }
        }
    }
}

@Composable
fun DiceChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f)

    LaunchedEffect(isPressed) {
        if (isPressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    val primaryColor = Color(0xFF3B82F6)

    Box(
        modifier = Modifier
            .size(62.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Brush.verticalGradient(
                    listOf(primaryColor.copy(alpha = 0.3f), primaryColor.copy(alpha = 0.05f))
                ) else Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                )
            )
            .border(
                1.dp,
                if (isSelected) primaryColor else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MultiDiceRow(
    diceLabel: String,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label do dado
        Text(
            text = diceLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 16.dp)
        )

        // Contador de quantidade
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (quantity > 0) onQuantityChange(quantity - 1)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Text(
                    text = "−",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (quantity > 0) Color.White else Color.White.copy(alpha = 0.2f)
                )
            }

            Text(
                text = "$quantity",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (quantity < 20) onQuantityChange(quantity + 1)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar",
                    modifier = Modifier.size(16.dp),
                    tint = if (quantity < 20) Color.White else Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

/**
 * Agrupa os resultados dos dados por tipo (d20, d12, etc.)
 */
@Composable
private fun groupRollsByDiceType(
    rolls: List<Int>,
    selections: Map<Int, Int>
): Map<String, List<Int>> {
    val grouped = mutableMapOf<String, List<Int>>()

    // Se não houver seleções (modo único ou expressão), tratamos como um grupo genérico ou tentamos inferir
    if (selections.isEmpty()) {
        grouped[stringResource(Res.string.dados_generic_label)] = rolls
        return grouped
    }

    val sortedSides = selections.keys.sortedDescending()
    var currentIndex = 0

    sortedSides.forEach { sides ->
        val quantity = selections[sides] ?: 0
        if (quantity > 0 && currentIndex < rolls.size) {
            val endIndex = (currentIndex + quantity).coerceAtMost(rolls.size)
            val diceRolls = rolls.subList(currentIndex, endIndex)
            grouped["d$sides"] = diceRolls
            currentIndex = endIndex
        }
    }

    return grouped
}
