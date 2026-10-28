package com.randomizer.personagens.export

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Paint
import android.graphics.Typeface
import com.randomizer.personagens.game.CharacterSummary
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.design.MedievalColors
import java.io.IOException
import java.io.OutputStream

/** Gera um PNG completo, sem capturar botões, diálogos ou áreas cortadas da tela. */
internal object CharacterImageExporter {
    fun writePng(state: GameState, output: OutputStream) {
        val summary = CharacterSummary.from(state)
        val bitmap = Bitmap.createBitmap(1080, 1280, Bitmap.Config.ARGB_8888)
        try {
            drawSummary(Canvas(bitmap), summary)
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                throw IOException("Não foi possível gerar o PNG.")
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun drawSummary(canvas: Canvas, summary: CharacterSummary) {
        val gold = MedievalColors.Bronze
        val purple = MedievalColors.Wine
        val white = MedievalColors.Ink
        val muted = MedievalColors.FadedInk
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        fun text(value: String, x: Float, y: Float, size: Float, color: Int,
                 bold: Boolean = false, centered: Boolean = false) {
            paint.style = Paint.Style.FILL
            paint.color = color
            paint.textSize = size
            paint.typeface = Typeface.create("serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
            paint.textAlign = if (centered) Paint.Align.CENTER else Paint.Align.LEFT
            canvas.drawText(value, x, y, paint)
        }

        paint.shader = LinearGradient(0f, 0f, 1080f, 0f, intArrayOf(
            MedievalColors.AgedPaper, MedievalColors.Parchment, MedievalColors.Paper,
            MedievalColors.Parchment, MedievalColors.AgedPaper,
        ), null, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, 1080f, 1280f, paint)
        paint.shader = null
        paint.color = MedievalColors.Bronze
        paint.alpha = 30
        repeat(240) { index ->
            canvas.drawCircle(((index * 73 + 17) % 1080).toFloat(),
                ((index * 137 + 41) % 1280).toFloat(), 1.2f, paint)
        }
        paint.alpha = 255
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawRect(24f, 24f, 1056f, 1256f, paint)
        paint.strokeWidth = 1f
        canvas.drawRect(34f, 34f, 1046f, 1246f, paint)
        text("RANDOMIZER", 64f, 112f, 36f, gold, bold = true)
        text(summary.mode, 64f, 202f, 64f, white, bold = true)
        text("Sete atributos. Uma nova história.", 64f, 258f, 30f, muted)

        val tile = 220f
        summary.groups.forEachIndexed { groupIndex, attributes ->
            val top = if (groupIndex == 0) 390f else 810f
            text("Grupo ${groupIndex + 1}", 64f, top - 40f, 28f, muted)
            val spacing = if (groupIndex == 0) 24f else (952f - 3 * tile) / 4
            attributes.forEachIndexed { index, attribute ->
                val left = if (groupIndex == 0) 64f + index * (tile + spacing)
                    else 64f + spacing + index * (tile + spacing)
                paint.color = MedievalColors.Paper
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(left, top, left + tile, top + tile, 8f, 8f, paint)
                paint.color = gold
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                canvas.drawRoundRect(left, top, left + tile, top + tile, 8f, 8f, paint)
                paint.strokeWidth = 1f
                canvas.drawRect(left + 10f, top + 10f, left + tile - 10f, top + tile - 10f, paint)
                text(attribute.value.toString(), left + tile / 2, top + 142f, 88f,
                    when (attribute.value) { 20 -> MedievalColors.Forest; 0 -> purple; else -> white },
                    bold = true, centered = true)
                text(attribute.name, left + tile / 2, top + tile + 46f, 30f, white, centered = true)
            }
        }
        text("Imagine a história por trás dos números.", 540f, 1200f, 30f, muted, centered = true)
    }
}
