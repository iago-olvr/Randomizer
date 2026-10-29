package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
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
import com.randomizer.personagens.game.CharacterRules
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
                        isRolling = false, onRoll = {}, onChange = {}, onCancel = {}, onExport = {},
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
        val instruction = compose.onNodeWithTag("stage-instruction").fetchSemanticsNode().boundsInRoot
        assertTrue(instruction.bottom < tiles[0].top)
        val action = compose.onNodeWithTag("board-action").performScrollTo().fetchSemanticsNode().boundsInRoot
        val lastRow = compose.onNodeWithTag("attribute-4", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(action.width, action.height, 1f)
        assertTrue(action.top > lastRow.bottom)
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
    fun personagemCompletoMostraTresAcoesSemUltimaRolagemNemSalvarPng() {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        repeat(7) { state = CharacterGenerator.enterPhysicalResult(state, it + 1) }
        var exports = 0
        var restarts = 0
        compose.setContent {
            RandomizerTheme {
                CharacterBoard(state, false, {}, {}, { restarts++ }, { exports++ })
            }
        }
        compose.onNodeWithText("Compartilhar").performScrollTo().performClick()
        val buttons = listOf("Editar nome", "Compartilhar", "Ver histórico").map {
            compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot
        }
        assertEquals(buttons[0].top, buttons[1].top, 1f)
        assertEquals(buttons[1].top, buttons[2].top, 1f)
        assertTrue(buttons[0].right < buttons[1].left)
        assertTrue(buttons[1].right < buttons[2].left)
        compose.onNodeWithText(state.lastEvent).assertDoesNotExist()
        compose.onNodeWithText("Salvar PNG").assertDoesNotExist()
        compose.onNodeWithTag("board-action").assertDoesNotExist()
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

    @Test
    fun seteDicasTemBotoesGrandesENaoMaximizamAtributosAoSeremAbertas() {
        val initial = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        val state = mutableStateOf(initial)
        compose.setContent {
            RandomizerTheme { CharacterBoard(state.value, false, {}, { state.value = it }, {}, {}) }
        }
        CharacterRules.attributes.forEachIndexed { index, attribute ->
            compose.onNodeWithTag("hint-$index").performScrollTo()
                .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
            compose.onNodeWithText(requireNotNull(attribute.extremes)).assertExists()
            compose.onNodeWithText("Entendi").performClick()
            compose.runOnIdle { assertEquals(initial, state.value) }
        }
    }
}
