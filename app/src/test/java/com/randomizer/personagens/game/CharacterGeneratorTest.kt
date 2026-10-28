package com.randomizer.personagens.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterGeneratorTest {
    @Test
    fun `vilao usa sete D20 em ordem sem criar zeros`() {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP)
        val results = listOf(1, 20, 7, 12, 3, 18, 9)
        results.forEach { value ->
            state = CharacterGenerator.roll(state) { sides ->
                assertEquals(20, sides)
                value
            }
        }
        assertEquals(results, state.values)
        assertEquals(Stage.COMPLETE, state.stage)
        assertEquals(7, state.filledCount)
    }

    @Test
    fun `D4 igual a quatro repete sem atribuir zero no primeiro grupo`() {
        val state = CharacterGenerator.chooseMaximum(extremes(), 1)
        val repeated = CharacterGenerator.enterPhysicalResult(state, 4)
        assertEquals(state.values, repeated.values)
        assertEquals(Stage.ROLL_MINIMUM, repeated.stage)
        assertEquals(1, repeated.filledCount)
        assertTrue(repeated.lastEvent.contains("novamente"))
        // A repetição não impõe limite de tentativas.
        assertEquals(repeated.values, CharacterGenerator.enterPhysicalResult(repeated, 4).values)
    }

    @Test
    fun `D4 do primeiro grupo mapeia os tres restantes para qualquer maximo`() {
        for (maximum in 0..3) {
            for (roll in 1..3) {
                val state = CharacterGenerator.chooseMaximum(extremes(), maximum)
                val next = CharacterGenerator.enterPhysicalResult(state, roll)
                assertEquals(20, next.values[maximum])
                assertEquals(0, next.values[state.remaining[roll - 1]])
                assertEquals(2, next.filledCount)
                assertEquals(Stage.ROLL_ATTRIBUTE, next.stage)
            }
        }
    }

    @Test
    fun `D4 do segundo grupo mapeia impares e pares para qualquer maximo`() {
        for (maximum in 4..6) {
            for (roll in 1..4) {
                val state = CharacterGenerator.chooseMaximum(secondGroup(), maximum)
                val next = CharacterGenerator.enterPhysicalResult(state, roll)
                val minimized = state.remaining[if (roll % 2 == 1) 0 else 1]
                assertEquals(20, next.values[maximum])
                assertEquals(0, next.values[minimized])
                assertEquals(Stage.ROLL_ATTRIBUTE, next.stage)
            }
        }
    }

    @Test
    fun `fluxo completo dos extremos preserva os valores entre grupos`() {
        var state = CharacterGenerator.chooseMaximum(extremes(), 2)
        state = CharacterGenerator.enterPhysicalResult(state, 2)
        state = CharacterGenerator.enterPhysicalResult(state, 1)
        state = CharacterGenerator.enterPhysicalResult(state, 20)
        assertEquals(listOf(1, 0, 20, 20, null, null, null), state.values)
        assertEquals(Stage.CHOOSE_MAXIMUM, state.stage)
        assertEquals(1, state.group)
        state = CharacterGenerator.chooseMaximum(state, 6)
        state = CharacterGenerator.enterPhysicalResult(state, 4)
        state = CharacterGenerator.enterPhysicalResult(state, 13)
        assertEquals(listOf(1, 0, 20, 20, 13, 0, 20), state.values)
        assertEquals(Stage.COMPLETE, state.stage)
        // D20 também pode produzir 20; não deve ser limitado para evitar máximos extras.
        assertEquals(3, state.values.count { it == 20 })
    }

    @Test
    fun `dados fisicos e digitais aplicam as mesmas regras`() {
        var app = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        var physical = extremes()
        app = CharacterGenerator.chooseMaximum(app, 0)
        physical = CharacterGenerator.chooseMaximum(physical, 0)
        for (roll in listOf(4, 3, 14, 9)) {
            app = CharacterGenerator.roll(app) { roll }
            physical = CharacterGenerator.enterPhysicalResult(physical, roll)
            assertEquals(app.copy(source = DiceSource.PHYSICAL), physical)
        }
        app = CharacterGenerator.chooseMaximum(app, 5)
        physical = CharacterGenerator.chooseMaximum(physical, 5)
        for (roll in listOf(2, 20)) {
            app = CharacterGenerator.roll(app) { roll }
            physical = CharacterGenerator.enterPhysicalResult(physical, roll)
            assertEquals(app.copy(source = DiceSource.PHYSICAL), physical)
        }
    }

    @Test
    fun `rejeita resultados fora do dado sem alterar estado`() {
        val d4 = CharacterGenerator.chooseMaximum(extremes(), 0)
        for (value in listOf(-1, 0, 5, 20)) {
            assertThrows(IllegalArgumentException::class.java) {
                CharacterGenerator.enterPhysicalResult(d4, value)
            }
        }
        val d20 = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        for (value in listOf(-1, 0, 21)) {
            assertThrows(IllegalArgumentException::class.java) {
                CharacterGenerator.enterPhysicalResult(d20, value)
            }
        }
        assertTrue(d20.values.all { it == null })
    }

    @Test
    fun `rejeita maximo de outro grupo e acoes fora da etapa`() {
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.chooseMaximum(extremes(), 4)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.enterPhysicalResult(extremes(), 1)
        }
        val chosen = CharacterGenerator.chooseMaximum(extremes(), 0)
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.chooseMaximum(chosen, 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.roll(chosen)
        }
        val digital = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP)
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.enterPhysicalResult(digital, 1)
        }
    }

    @Test
    fun `personagem completo nao aceita novas rolagens`() {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        repeat(7) { state = CharacterGenerator.enterPhysicalResult(state, 10) }
        assertThrows(IllegalArgumentException::class.java) {
            CharacterGenerator.enterPhysicalResult(state, 10)
        }
    }

    @Test
    fun `novo personagem tem valores independentes`() {
        val original = extremes()
        val changed = CharacterGenerator.chooseMaximum(original, 0)
        val fresh = extremes()
        assertTrue(original.values.all { it == null })
        assertTrue(fresh.values.all { it == null })
        assertNotSame(original.values, changed.values)
    }

    private fun extremes() = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.PHYSICAL)

    private fun secondGroup(): GameState {
        var state = CharacterGenerator.chooseMaximum(extremes(), 0)
        for (result in listOf(1, 8, 12)) {
            state = CharacterGenerator.enterPhysicalResult(state, result)
        }
        return state
    }
}
