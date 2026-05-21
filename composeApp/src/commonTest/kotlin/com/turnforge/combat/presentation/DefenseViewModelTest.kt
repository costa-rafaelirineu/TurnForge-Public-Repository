package com.turnforge.combat.presentation

import com.turnforge.combat.domain.model.DefensePreset
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefenseViewModelTest {

    private val testPresets = listOf(
        DefensePreset("1", "Dodge", "🛡️", "Dodge attack", duration = 1),
        DefensePreset("2", "Block", "🧱", "Block attack", duration = 1),
        DefensePreset("3", "Shield", "🪄", "Shield spell", duration = 1)
    )

    @Test
    fun testInitialPresets() = runBlocking {
        val viewModel = DefenseViewModel(testPresets)
        delay(200)
        assertEquals(3, viewModel.presets.size)
    }

    @Test
    fun testSelectPreset() = runBlocking {
        val viewModel = DefenseViewModel(testPresets)
        delay(200)
        val second = viewModel.presets[1]
        viewModel.selectPreset(second)
        assertEquals(second, viewModel.currentPreset)
    }

    @Test
    fun testAddPreset() = runBlocking {
        val viewModel = DefenseViewModel(testPresets)
        delay(200)
        val newPreset = DefensePreset("4", "Wall", "🧱", "Extra defense")
        viewModel.addPreset(newPreset)
        assertEquals(4, viewModel.presets.size)
        assertTrue(viewModel.presets.contains(newPreset))
    }

    @Test
    fun testRemovePreset() = runBlocking {
        val viewModel = DefenseViewModel(testPresets)
        delay(200)
        val firstId = viewModel.presets.first().id
        viewModel.removePreset(firstId)
        assertEquals(2, viewModel.presets.size)
        assertFalse(viewModel.presets.any { it.id == firstId })
    }
}
