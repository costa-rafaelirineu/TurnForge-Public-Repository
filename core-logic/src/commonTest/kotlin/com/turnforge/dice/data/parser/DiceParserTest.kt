package com.turnforge.dice.data.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DiceParserTest {

    private val parser = DiceParserImpl()

    @Test
    fun testParseSimpleDice() {
        val result = parser.parse("d20")
        assertNotNull(result)
        assertEquals(1, result.groups.size)
        assertEquals(1, result.groups[0].quantity)
        assertEquals(20, result.groups[0].sides)
        assertEquals(0, result.modifier)
    }

    @Test
    fun testParseDiceWithQuantity() {
        val result = parser.parse("2d6")
        assertNotNull(result)
        assertEquals(1, result.groups.size)
        assertEquals(2, result.groups[0].quantity)
        assertEquals(6, result.groups[0].sides)
    }

    @Test
    fun testParseDiceWithModifier() {
        val result = parser.parse("1d8 + 5")
        assertNotNull(result)
        assertEquals(5, result.modifier)
    }

    @Test
    fun testParseDiceWithNegativeModifier() {
        val result = parser.parse("d10-2")
        assertNotNull(result)
        assertEquals(-2, result.modifier)
    }

    @Test
    fun testParseMultipleDiceGroups() {
        val result = parser.parse("2d6 + 1d4 + 10")
        assertNotNull(result)
        assertEquals(2, result.groups.size)
        assertEquals(2, result.groups[0].quantity)
        assertEquals(6, result.groups[0].sides)
        assertEquals(1, result.groups[1].quantity)
        assertEquals(4, result.groups[1].sides)
        assertEquals(10, result.modifier)
    }

    @Test
    fun testParseWithSpacesAndCase() {
        val result = parser.parse("  3 D 12  +  7  ")
        assertNotNull(result)
        assertEquals(3, result.groups[0].quantity)
        assertEquals(12, result.groups[0].sides)
        assertEquals(7, result.modifier)
    }

    @Test
    fun testParseInvalidInputs() {
        assertNull(parser.parse(""))
        assertNull(parser.parse("abc"))
        assertNull(parser.parse("10")) // Apenas bônus sem dado não é permitido pelo parser atual
        assertNull(parser.parse("d"))
    }

    @Test
    fun testParseComplexExpression() {
        // Testando uma mistura de espaços e múltiplos modificadores
        val result = parser.parse("1d20 + 2 - 1 + 5")
        assertNotNull(result)
        assertEquals(6, result.modifier) // 2 - 1 + 5 = 6
    }
}
