package com.randomizer.personagens.ui

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class RandomizerAppTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun resultadoFisicoInvalidoNaoPodeSerConfirmado() {
        compose.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performScrollTo().performClick()
        compose.onNodeWithText("Resultado do D20").performScrollTo().performTextInput("0")
        compose.onNodeWithText("Confirmar resultado").assertIsNotEnabled()
    }

    @Test
    fun progressoSobreviveARestauracaoDaTela() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Dados físicos").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performScrollTo().performClick()
        compose.onNodeWithText("Resultado do D20").performScrollTo().performTextInput("17")
        compose.onNodeWithText("Confirmar resultado").assertIsEnabled().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("D20 para Dinheiro").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("17", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
    }
}
