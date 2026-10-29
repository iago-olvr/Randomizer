package com.randomizer.personagens.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class PageTransitionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun cancelarPeloXVoltaDiretamenteParaConfiguracao() {
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Criar personagem").performClick()
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("page-board").assertExists()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithTag("page-setup").assertDoesNotExist()
        compose.onNodeWithContentDescription("Cancelar").performScrollTo().performClick()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithText("Recomeçar").assertDoesNotExist()
        compose.onNodeWithTag("page-board").assertDoesNotExist()
        compose.onNodeWithText("Criar personagem").assertIsDisplayed()
    }

    @Test
    fun recriarDuranteTransicaoMantemModoEAtributoAtual() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Criar personagem").performClick()
        compose.mainClock.advanceTimeBy(64)
        restoration.emulateSavedInstanceStateRestore()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithTag("page-setup").assertDoesNotExist()
        compose.onNodeWithText("Vilão").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("D20 para Inteligência").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun ultimoAtributoAbreResultadoSemPerderValores() {
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        for (result in 1..7) {
            compose.onNodeWithText("Resultado do D20").performScrollTo().performTextInput(result.toString())
            if (result == 7) compose.mainClock.autoAdvance = false
            compose.onNodeWithText("Confirmar resultado").performScrollTo().performClick()
        }
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithTag("page-board").assertDoesNotExist()
        compose.onNodeWithTag("page-result").assertExists()
        compose.onNodeWithText("Compartilhar").performScrollTo().assertIsDisplayed()
        for (value in 1..7) {
            compose.onNodeWithText(value.toString(), useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        }
    }
}
