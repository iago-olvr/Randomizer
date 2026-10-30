package com.randomizer.personagens.export

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.GameState
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

@RunWith(AndroidJUnit4::class)
class CharacterImageExporterTest {
    @Test
    fun imagemIncluiBordaDouradaERachaduraNosExtremos() {
        var state = CharacterGenerator.start(CreationMode.EXTREMES, DiceSource.APP)
        while (state.stage != com.randomizer.personagens.game.Stage.COMPLETE) {
            state = if (state.stage == com.randomizer.personagens.game.Stage.CHOOSE_MAXIMUM)
                CharacterGenerator.chooseMaximum(state, state.remaining.first())
            else CharacterGenerator.roll(state) { 1 }
        }
        val output = ByteArrayOutputStream()
        CharacterImageExporter.writePng(state, output)
        val bitmap = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size())
        try {
            // Centro da borda superior do primeiro 20 e um ponto da rachadura do 0.
            assertEquals(0xFFC99A28.toInt(), bitmap.getPixel(174, 390))
            assertEquals(0xFF67594B.toInt(), bitmap.getPixel(348, 421))
            assertEquals(com.randomizer.personagens.design.MedievalColors.FilledCard, bitmap.getPixel(174, 434))
            fun hasNumberColor(left: Int, color: Int) = (left + 66..left + 154).any { x ->
                (478..531).any { y -> bitmap.getPixel(x, y) == color }
            }
            assertTrue(hasNumberColor(64, com.randomizer.personagens.design.MedievalColors.MaximumGold))
            assertTrue(hasNumberColor(308, com.randomizer.personagens.design.MedievalColors.RuinStone))
            assertFalse(hasNumberColor(308, com.randomizer.personagens.design.MedievalColors.Wine))
            assertFalse(hasNumberColor(308, com.randomizer.personagens.design.MedievalColors.Paper))
            assertTrue(hasNumberColor(552, com.randomizer.personagens.design.MedievalColors.Paper))
        } finally {
            bitmap.recycle()
        }
    }

    @Test
    fun geraImagemPngCompletaQuePodeSerReaberta() {
        val output = ByteArrayOutputStream()
        CharacterImageExporter.writePng(complete(), output)
        val bytes = output.toByteArray()
        assertArrayEquals(byteArrayOf(-119, 80, 78, 71, 13, 10, 26, 10), bytes.take(8).toByteArray())
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        assertNotNull(bitmap)
        try {
            assertEquals(1080, bitmap.width)
            assertEquals(1280, bitmap.height)
        } finally {
            bitmap.recycle()
        }
    }

    @Test
    fun personagemIncompletoNaoGravaDados() {
        val output = ByteArrayOutputStream()
        assertThrows(IllegalArgumentException::class.java) {
            CharacterImageExporter.writePng(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.APP), output)
        }
        assertEquals(0, output.size())
    }

    @Test
    fun falhaNaGravacaoPermiteNovaTentativa() {
        val state = complete()
        val failing = object : OutputStream() {
            override fun write(value: Int) { throw IOException("Destino indisponível") }
        }
        assertThrows(IOException::class.java) { CharacterImageExporter.writePng(state, failing) }
        val retry = ByteArrayOutputStream()
        CharacterImageExporter.writePng(state, retry)
        assertNotNull(BitmapFactory.decodeByteArray(retry.toByteArray(), 0, retry.size()).also { it?.recycle() })
        assertEquals(7, state.filledCount)
    }

    private fun complete(): GameState {
        var state = CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)
        repeat(7) { state = CharacterGenerator.enterPhysicalResult(state, it + 1) }
        return state
    }
}
