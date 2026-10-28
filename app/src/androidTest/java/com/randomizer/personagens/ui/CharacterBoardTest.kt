package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.Stage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharacterBoardTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun quatroCartoesEmCimaTresEmbaixoEAcaoCircularNaBase() {
        compose.setContent {
            RandomizerTheme {
                Box(Modifier.requiredSize(360.dp, 740.dp)) {
                    CharacterBoard(
                        game = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP),
                        isRolling = false, onRoll = {}, onChange = {}, onNew = {}, onExport = {},
                    )
                }
            }
        }
        val tiles = (0..6).map {
            compose.onNodeWithTag("attribute-$it", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        }
        tiles.forEach { assertEquals(it.width, it.height, 1f) }
        (1..3).forEach { assertEquals(tiles[0].top, tiles[it].top, 1f) }
        (5..6).forEach { assertEquals(tiles[4].top, tiles[it].top, 1f) }
        assertTrue(tiles[4].top > tiles[0].bottom)
        val action = compose.onNodeWithTag("board-action").fetchSemanticsNode().boundsInRoot
        assertEquals(action.width, action.height, 1f)
        assertTrue(action.top > tiles[4].bottom)
    }

    @Test
    fun tocarNoCartaoMaximizaEAHabilitaRolagemDoD4() {
        val state = mutableStateOf(CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP))
        compose.setContent {
            RandomizerTheme {
                CharacterBoard(state.value, false, {}, { state.value = it }, {}, {})
            }
        }
        compose.onNodeWithTag("board-action").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Inteligência").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(20, state.value.values[0])
            assertEquals(Stage.ROLL_MINIMUM, state.value.stage)
        }
        compose.onNodeWithText("Rolar D4").performScrollTo().assertIsEnabled()
    }

    @Test
    fun personagemCompletoOfereceExportarSemReiniciar() {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        repeat(7) { state = CharacterGenerator.enterPhysicalResult(state, it + 1) }
        var exports = 0
        var restarts = 0
        compose.setContent {
            RandomizerTheme {
                CharacterBoard(state, false, {}, {}, { restarts++ }, { exports++ })
            }
        }
        compose.onNodeWithText("Exportar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, exports)
            assertEquals(0, restarts)
            assertEquals((1..7).toList(), state.values)
        }
        compose.onNodeWithText("Novo").assertDoesNotExist()
        compose.onNodeWithText("Dados físicos").assertDoesNotExist()
        compose.onNodeWithText("7 de 7 atributos preenchidos").assertDoesNotExist()
    }

    @Test
    fun cabecalhoMostraApenasModoPersonagem() {
        val state = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        compose.setContent {
            RandomizerTheme { CharacterBoard(state, false, {}, {}, {}, {}) }
        }
        compose.onNodeWithText("Personagem").performScrollTo().assertExists()
        compose.onNodeWithText("Dados no app").assertDoesNotExist()
        compose.onNodeWithText("0 de 7 atributos preenchidos").assertDoesNotExist()
    }
}
