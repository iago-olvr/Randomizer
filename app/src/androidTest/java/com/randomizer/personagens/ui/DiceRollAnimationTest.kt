package com.randomizer.personagens.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DiceRollAnimationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun d4RevelaOResultadoAntesDeConcluirUmaUnicaVez() {
        var completions = 0
        compose.mainClock.autoAdvance = false
        compose.setContent {
            RandomizerTheme { DiceRollDialog(sides = 4, result = 4) { completions++ } }
        }
        compose.onNodeWithText("Rolando D4…").assertIsDisplayed()
        compose.mainClock.advanceTimeBy(900)
        compose.runOnIdle { assertEquals(0, completions) }
        compose.mainClock.advanceTimeBy(350)
        compose.onNodeWithContentDescription("D4: 4").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, completions) }
        compose.mainClock.advanceTimeBy(1000)
        compose.runOnIdle { assertEquals(1, completions) }
        compose.mainClock.advanceTimeBy(2000)
        compose.runOnIdle { assertEquals(1, completions) }
    }

    @Test
    fun d20MantemResultadoAoRestaurarEPreencheSomenteUmAtributo() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { RandomizerApp() }
        compose.onNodeWithText("Vilão").performScrollTo().performClick()
        compose.onNodeWithText("Criar personagem").performClick()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Rolar D20").performScrollTo().performClick()
        compose.mainClock.advanceTimeBy(1250)
        val resultNode = compose.onNodeWithContentDescription("D20: ", substring = true)
        val description = resultNode.fetchSemanticsNode().config[SemanticsProperties.ContentDescription].single()
        val result = description.substringAfter(": ")
        restoration.emulateSavedInstanceStateRestore()
        compose.mainClock.advanceTimeBy(1250)
        compose.onNodeWithContentDescription(description).assertIsDisplayed()
        compose.mainClock.advanceTimeBy(1000)
        compose.onNodeWithContentDescription(description).assertDoesNotExist()
        compose.onNodeWithText("1 de 7 atributos preenchidos").assertDoesNotExist()
        compose.onNodeWithText("D20: $result → Inteligência.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("D20 para Dinheiro").performScrollTo().assertIsDisplayed()
    }
}
