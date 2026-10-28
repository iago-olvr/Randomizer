package com.randomizer.personagens.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CharacterSummaryTest {
    @Test
    fun `exportacao preserva nomes valores e ordem dos dois grupos`() {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        val values = listOf(1, 20, 7, 12, 3, 18, 9)
        values.forEach { state = CharacterGenerator.enterPhysicalResult(state, it) }
        val summary = CharacterSummary.from(state)
        assertEquals("Vilão", summary.mode)
        assertEquals(listOf(4, 3), summary.groups.map { it.size })
        assertEquals(listOf("Inteligência", "Dinheiro", "Saúde", "Beleza", "Físico", "Altura", "Cabelo"),
            summary.groups.flatten().map { it.name })
        assertEquals(values, summary.groups.flatten().map { it.value })
    }

    @Test
    fun `personagem exporta inclusive extremos zero e vinte`() {
        var state = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.PHYSICAL)
        state = CharacterGenerator.chooseMaximum(state, 0)
        for (value in listOf(1, 1, 20)) state = CharacterGenerator.enterPhysicalResult(state, value)
        state = CharacterGenerator.chooseMaximum(state, 4)
        for (value in listOf(4, 19)) state = CharacterGenerator.enterPhysicalResult(state, value)
        val summary = CharacterSummary.from(state)
        assertEquals("Personagem", summary.mode)
        assertEquals(listOf(20, 0, 1, 20, 20, 19, 0), summary.groups.flatten().map { it.value })
    }

    @Test
    fun `nao exporta personagem incompleto`() {
        val state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP)
        assertThrows(IllegalArgumentException::class.java) { CharacterSummary.from(state) }
        assertThrows(IllegalArgumentException::class.java) {
            CharacterSummary.from(state.copy(stage = Stage.COMPLETE))
        }
    }
}
