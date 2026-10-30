package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import com.randomizer.personagens.design.MedievalColors
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
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

    @Test fun particulasContinuamEmMovimentoEAoRemoverVinteParam() {
        val initial = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        val state = mutableStateOf(CharacterGenerator.chooseMaximum(initial, 0))
        compose.mainClock.autoAdvance = false
        compose.setContent {
            RandomizerTheme { CharacterBoard(state.value, false, {}, {}, {}, {}) }
        }
        fun pixels() = compose.onNodeWithTag("attribute-0", useUnmergedTree = true)
            .captureToImage().toPixelMap()
        fun changed(first: androidx.compose.ui.graphics.PixelMap,
                    second: androidx.compose.ui.graphics.PixelMap): Boolean =
            (0 until first.height).any { y ->
                (0 until first.width).any { x -> first[x, y] != second[x, y] }
            }
        compose.mainClock.advanceTimeBy(350)
        val first = pixels()
        compose.mainClock.advanceTimeBy(500)
        assertTrue(changed(first, pixels()))
        compose.runOnIdle { state.value = initial }
        compose.mainClock.advanceTimeBy(100)
        val empty = pixels()
        compose.mainClock.advanceTimeBy(500)
        org.junit.Assert.assertFalse(changed(empty, pixels()))
    }

    @Test fun preenchidosEscurecemInclusiveZeroEVinteEVaziosPreservamOFundo() {
        val state = mutableStateOf(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP))
        compose.setContent {
            RandomizerTheme { CharacterBoard(state.value, false, {}, {}, {}, {}) }
        }
        fun background(index: Int): Color {
            val pixels = compose.onNodeWithTag("attribute-$index", useUnmergedTree = true)
                .performScrollTo().captureToImage().toPixelMap()
            return pixels[pixels.width / 2, pixels.height / 5]
        }
        assertEquals(Color(MedievalColors.Paper), background(1))
        compose.runOnIdle {
            state.value = state.value.copy(values = listOf(null, 12, 20, 0, null, null, null))
        }
        (1..3).forEach { assertEquals(Color(0xFF39393B), background(it)) }
        assertEquals(Color(MedievalColors.Paper), background(4))
        compose.onNodeWithContentDescription("Saúde: 20, máximo dourado", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Beleza: 0, ruína", useUnmergedTree = true).assertExists()
    }

    @Test fun nomeSubstituiFraseERemoverNomeRestauraFrase() {
        val state = (1..7).fold(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP)) {
            game, value -> CharacterGenerator.roll(game) { value }
        }
        val name = mutableStateOf("")
        val phrase = "Imaginem juntos a personalidade e a história desse personagem."
        compose.setContent {
            RandomizerTheme {
                CharacterBoard(state, false, {}, {}, {}, {}, characterName = name.value)
            }
        }
        compose.onNodeWithText(phrase).assertExists()
        compose.runOnIdle { name.value = "Aurora" }
        compose.onNodeWithText("Aurora").assertExists()
        compose.onNodeWithText(phrase).assertDoesNotExist()
        compose.runOnIdle { name.value = "" }
        compose.onNodeWithText(phrase).assertExists()
    }

    @Test fun rodapePermaneceFixoAoRolarEmTelaBaixaComFonteGrande() {
        compose.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(1f, 1.5f)
            ) {
                RandomizerTheme {
                    Box(Modifier.requiredSize(360.dp, 480.dp)) {
                        CharacterBoard(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP),
                            false, {}, {}, {}, {})
                    }
                }
            }
        }
        val before = compose.onNodeWithTag("board-footer").fetchSemanticsNode().boundsInRoot
        compose.onNodeWithTag("board-content").performTouchInput { swipeUp() }
        val after = compose.onNodeWithTag("board-footer").fetchSemanticsNode().boundsInRoot
        assertEquals(before, after)
        compose.onNodeWithTag("board-action").assertIsDisplayed()
        compose.onNodeWithTag("hint-6").performScrollTo().assertIsDisplayed()
    }

    @Test fun extremosPossuemIdentificacaoAcessivel() {
        var state = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        state = CharacterGenerator.chooseMaximum(state, 0)
        state = CharacterGenerator.roll(state) { 1 }
        compose.setContent {
            RandomizerTheme { CharacterBoard(state, false, {}, {}, {}, {}) }
        }
        compose.onNodeWithContentDescription("Inteligência: 20, máximo dourado", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Dinheiro: 0, ruína", useUnmergedTree = true).assertExists()
    }

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
        assertTrue(tiles[4].top - tiles[0].bottom < tiles[0].width * 2)
        val instruction = compose.onNodeWithTag("stage-instruction").fetchSemanticsNode().boundsInRoot
        assertTrue(instruction.bottom < tiles[0].top)
        val action = compose.onNodeWithTag("board-action").fetchSemanticsNode().boundsInRoot
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
        compose.onNodeWithTag("board-action").assertIsNotEnabled()
        compose.onNodeWithText("Inteligência").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(20, state.value.values[0])
            assertEquals(Stage.ROLL_MINIMUM, state.value.stage)
        }
        compose.onNodeWithText("Rolar D4").assertIsEnabled()
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
        compose.onNodeWithText("Compartilhar").performClick()
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
