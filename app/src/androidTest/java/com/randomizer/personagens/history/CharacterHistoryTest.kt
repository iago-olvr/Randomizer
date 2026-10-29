package com.randomizer.personagens.history

import androidx.test.platform.app.InstrumentationRegistry
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CharacterHistoryTest {
    @get:Rule val folder = TemporaryFolder(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir)

    private fun character(id: String, time: Long, name: String) = SavedCharacter(id, time, name,
        (1..7).fold(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)) {
            state, value -> CharacterGenerator.enterPhysicalResult(state, value)
        })

    @Test fun reabreArquivoPreservaValoresENomesEOrdenaMaisRecentesPrimeiro() {
        val file = File(folder.root, "history.json")
        val store = CharacterHistory(file)
        assertEquals(emptyList<SavedCharacter>(), store.load())
        store.save(character("first", 1, "João"))
        store.save(character("second", 2, ""))
        val restored = CharacterHistory(file).load()
        assertEquals(listOf("second", "first"), restored.map { it.id })
        assertEquals("João", restored.last().name)
        assertEquals((1..7).toList(), restored.last().game.values)
        assertEquals(DiceSource.PHYSICAL, restored.last().game.source)
    }

    @Test fun editarNomeAtualizaMesmoRegistroSemDuplicar() {
        val store = CharacterHistory(File(folder.root, "history.json"))
        val original = character("same", 1, "")
        store.save(original)
        store.save(original.copy(name = "Novo nome"))
        assertEquals(listOf(original.copy(name = "Novo nome")), store.load())
    }

    @Test fun arquivoInvalidoNaoESobrescrito() {
        val file = folder.newFile("history.json")
        file.writeText("dados inválidos")
        assertThrows(Exception::class.java) { CharacterHistory(file).save(character("new", 1, "")) }
        assertEquals("dados inválidos", file.readText())
        assertThrows(Exception::class.java) { CharacterHistory(file).delete("new") }
        assertEquals("dados inválidos", file.readText())
    }

    @Test fun excluirPreservaOutrosPersonagensEAExclusaoPersisteAoReabrir() {
        val file = File(folder.root, "history.json")
        val store = CharacterHistory(file)
        val first = character("first", 1, "Um")
        val second = character("second", 2, "Dois")
        store.save(first)
        store.save(second)
        assertEquals(listOf(second), store.delete(first.id))
        assertEquals(listOf(second), CharacterHistory(file).load())
        assertEquals(listOf(second), store.delete("missing"))
        assertEquals(emptyList<SavedCharacter>(), store.delete(second.id))
        assertEquals(emptyList<SavedCharacter>(), CharacterHistory(file).load())
    }
}
