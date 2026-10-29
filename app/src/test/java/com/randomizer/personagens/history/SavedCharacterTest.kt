package com.randomizer.personagens.history

import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SavedCharacterTest {
    private fun complete() = (1..7).fold(
        CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL),
    ) { state, value -> CharacterGenerator.enterPhysicalResult(state, value) }

    @Test fun aceitaNomeOpcionalEAcentosSemAlterarAtributos() {
        val state = complete()
        assertEquals(state, SavedCharacter("id", 1, "", state).game)
        assertEquals("João, o Sábio", SavedCharacter("id", 1, "João, o Sábio", state).name)
    }

    @Test fun rejeitaHistoricoIncompletoENomeAcimaDoLimite() {
        assertThrows(IllegalArgumentException::class.java) {
            SavedCharacter("id", 1, "", CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP))
        }
        assertThrows(IllegalArgumentException::class.java) { SavedCharacter("id", 1, "a".repeat(61), complete()) }
        assertThrows(IllegalArgumentException::class.java) { SavedCharacter("", 1, "", complete()) }
    }
}
