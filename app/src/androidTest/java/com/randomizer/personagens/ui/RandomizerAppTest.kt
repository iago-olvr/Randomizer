package com.randomizer.personagens.ui

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextClearance
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class RandomizerAppTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun rodapeFixoAbreAjudaDetalhadaEPreservaOpcoesAoVoltar() {
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        val footer = compose.onNodeWithTag("home-actions").fetchSemanticsNode().boundsInRoot
        val actions = listOf("Como jogar", "Criar personagem", "Histótico").map {
            compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot
        }
        val icons = (0..2).map {
            compose.onNodeWithTag("home-icon-$it", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        }
        assertTrue(icons[1].width > icons[0].width)
        assertTrue(icons[1].height > icons[2].height)
        assertTrue(actions.all { it.top >= footer.top && it.bottom <= footer.bottom })
        compose.onNodeWithText("RANDOMIZER").assertDoesNotExist()
        compose.onNodeWithText("Use os dados para descobrir os atributos. Depois, dê vida ao resultado.").assertDoesNotExist()
        compose.onNodeWithText("Tipo de Personagem").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Como jogar").performClick()
        compose.onNodeWithTag("page-help").assertExists()
        compose.onNodeWithContentDescription("Voltar").assertIsDisplayed()
        compose.onNodeWithText("Voltar").assertDoesNotExist()
        compose.onNodeWithText("Modo Personagem — segundo grupo").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Nome, compartilhamento e histórico").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar às opções").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        compose.onNodeWithText("Resultado do D20").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Vilão").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun personagemNomeadoPodeSerReabertoPeloHistorico() {
        val name = "Herói ${System.nanoTime()}"
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        repeat(7) {
            compose.onNodeWithText("Resultado do D20").performScrollTo().performTextClearance()
            compose.onNodeWithText("Resultado do D20").performTextInput("17")
            compose.onNodeWithText("Confirmar resultado").performScrollTo().performClick()
        }
        compose.waitUntil(5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Salvo no histórico local"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Editar nome").performScrollTo().performClick()
        compose.onNodeWithText("Nome (opcional)").performTextInput(name)
        compose.onNodeWithText("Salvar nome").performClick()
        compose.waitUntil(5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasText("Salvo no histórico local"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Cancelar").performScrollTo().performClick()
        compose.onNodeWithText("Histótico").performClick()
        compose.onNodeWithText(name).performScrollTo().performClick()
        compose.onNodeWithText(name).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Compartilhar").performScrollTo().assertIsEnabled()
    }

    @Test
    fun resultadoFisicoInvalidoNaoPodeSerConfirmado() {
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        compose.onNodeWithText("Resultado do D20").performScrollTo().performTextInput("0")
        compose.onNodeWithText("Confirmar resultado").assertIsNotEnabled()
    }

    @Test
    fun progressoSobreviveARestauracaoDaTela() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        compose.onNodeWithText("Resultado do D20").performScrollTo().performTextInput("17")
        compose.onNodeWithText("Confirmar resultado").assertIsEnabled().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("D20 para Dinheiro").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("17", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
    }
}
