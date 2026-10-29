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
        var shares = 0
        var deleted: SavedCharacter? = null
        compose.setContent {
            RandomizerTheme {
                HistoryScreen(characters.value, true, {}, {},
                    onShare = { shares++ },
                    onDelete = { deleted = it; characters.value = emptyList() })
            }
        }
        compose.onNodeWithTag("history-item-test-id").performScrollTo().performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, shares)
            assertEquals(listOf(saved), characters.value)
            assertEquals(null, deleted)
        }
        compose.onNodeWithTag("history-item-test-id").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(saved, deleted) }
        compose.onNodeWithText("Nenhum personagem salvo ainda.").assertExists()
    }

    @Test fun botoesComSetasExecutamMesmasAcoesSemAbrirFicha() {
        var shares = 0
        var deletes = 0
        var opens = 0
        compose.setContent {
            RandomizerTheme {
                HistoryScreen(listOf(saved), true, {}, { opens++ }, { shares++ }, { deletes++ })
            }
        }
        compose.onNodeWithText("Compartilhar →").performScrollTo().performClick()
        compose.onNodeWithText("← Excluir").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, shares)
            assertEquals(1, deletes)
            assertEquals(0, opens)
        }
    }
}
