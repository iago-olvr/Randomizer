package com.randomizer.personagens.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.history.SavedCharacter
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {
    @get:Rule val compose = createComposeRule()

    private val saved = SavedCharacter("test-id", 1, "Personagem de teste",
        (1..7).fold(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)) {
            state, value -> CharacterGenerator.enterPhysicalResult(state, value)
        })

    @Test fun deslizarParaDireitaCompartilhaSemExcluirEParaEsquerdaExclui() {
        val characters = mutableStateOf(listOf(saved))
        val enabled = mutableStateOf(true)
        var shares = 0
        var deleted: SavedCharacter? = null
        compose.setContent {
            RandomizerTheme {
                HistoryScreen(characters.value, enabled.value, {}, {},
                    onShare = { shares++; enabled.value = false },
                    onDelete = { deleted = it; characters.value = emptyList() })
            }
        }
        compose.onNodeWithTag("history-item-test-id").performScrollTo().performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, shares)
            assertEquals(listOf(saved), characters.value)
            assertEquals(null, deleted)
            enabled.value = true
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, shares) }
        compose.onNodeWithTag("history-item-test-id").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(null, deleted) }
        compose.onNodeWithText("Excluir personagem?").assertExists()
        compose.onNodeWithText("Excluir").performClick()
        compose.runOnIdle { assertEquals(saved, deleted) }
        compose.onNodeWithText("Nenhum personagem salvo ainda.").assertExists()
    }

    @Test fun textosDasSetasNaoCompartilhamEExcluirPodeSerCancelado() {
        var shares = 0
        var deletes = 0
        var opens = 0
        compose.setContent {
            RandomizerTheme {
                HistoryScreen(listOf(saved), true, {}, { opens++ }, { shares++ }, { deletes++ })
            }
        }
        compose.onNodeWithText("Compartilhar →").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(0, shares)
            assertEquals(0, deletes)
            assertEquals(1, opens)
        }
        compose.onNodeWithTag("history-item-test-id").performTouchInput { swipeLeft() }
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(0, deletes) }
        compose.onNodeWithTag("history-item-test-id").performTouchInput { swipeRight() }
        compose.runOnIdle { assertEquals(1, shares) }
        compose.onNodeWithTag("history-item-test-id").performTouchInput { swipeRight() }
        compose.runOnIdle { assertEquals(2, shares) }
    }
}
