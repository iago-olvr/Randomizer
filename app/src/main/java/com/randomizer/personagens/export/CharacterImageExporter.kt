package com.randomizer.personagens.export

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import com.randomizer.personagens.game.CharacterSummary
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.design.MedievalColors
import com.randomizer.personagens.design.RuinMark
import java.io.IOException
import java.io.OutputStream

/** Gera um PNG completo, sem capturar botões, diálogos ou áreas cortadas da tela. */
internal object CharacterImageExporter {
    fun writePng(state: GameState, output: OutputStream, name: String = "") {
        val summary = CharacterSummary.from(state)
        val bitmap = Bitmap.createBitmap(1080, 1280, Bitmap.Config.ARGB_8888)
        try {
            drawSummary(Canvas(bitmap), summary, name.trim().take(60))
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                throw IOException("Não foi possível gerar o PNG.")
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun drawSummary(canvas: Canvas, summary: CharacterSummary, name: String) {
        val gold = MedievalColors.Bronze
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
        if (name.isNotEmpty()) {
            // Ajusta o nome inteiro à largura disponível, inclusive com caracteres largos.
            paint.typeface = Typeface.create("serif", Typeface.BOLD)
            paint.textSize = 38f
            val size = 38f * minOf(1f, 952f / paint.measureText(name))
            text(name, 64f, 320f, size, white, bold = true)
        }

        val tile = 220f
        summary.groups.forEachIndexed { groupIndex, attributes ->
            val top = if (groupIndex == 0) 390f else 810f
            text("Grupo ${groupIndex + 1}", 64f, top - 40f, 28f, muted)
            val spacing = if (groupIndex == 0) 24f else (952f - 3 * tile) / 4
            attributes.forEachIndexed { index, attribute ->
                val left = if (groupIndex == 0) 64f + index * (tile + spacing)
                    else 64f + spacing + index * (tile + spacing)
                paint.color = MedievalColors.FilledCard
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(left, top, left + tile, top + tile, 8f, 8f, paint)
                paint.color = if (attribute.value == 20) MedievalColors.MaximumGold else gold
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = if (attribute.value == 20) 7f else 3f
                canvas.drawRoundRect(left, top, left + tile, top + tile, 8f, 8f, paint)
                paint.strokeWidth = 1f
                canvas.drawRect(left + 10f, top + 10f, left + tile - 10f, top + tile - 10f, paint)
                if (attribute.value == 20) {
                    paint.color = 0xFFFFE8A0.toInt()
                    paint.strokeWidth = 4f
                    listOf(left + 30f to top + 30f, left + tile - 30f to top + tile - 30f).forEach { (x, y) ->
                        canvas.drawLine(x - 12f, y, x + 12f, y, paint)
                        canvas.drawLine(x, y - 12f, x, y + 12f, paint)
                    }
                } else if (attribute.value == 0) {
                    paint.color = 0xFF67594B.toInt()
                    paint.strokeWidth = 4f
                    val crack = Path().apply {
                        moveTo(left + tile * .22f, top)
                        lineTo(left + tile * .18f, top + tile * .14f)
                        lineTo(left + tile * .29f, top + tile * .23f)
                        lineTo(left + tile * .23f, top + tile * .36f)
                        moveTo(left + tile, top + tile * .62f)
                        lineTo(left + tile * .82f, top + tile * .69f)
                        lineTo(left + tile * .88f, top + tile * .81f)
                        lineTo(left + tile * .72f, top + tile)
                    }
                    canvas.drawPath(crack, paint)
                    paint.style = Paint.Style.FILL
                    paint.color = MedievalColors.RuinStone
                    RuinMark.fragments.forEach { points ->
                        canvas.drawPath(Path().apply {
                            points.forEachIndexed { position, (x, y) ->
                                if (position == 0) moveTo(left + tile * x, top + tile * y)
                                else lineTo(left + tile * x, top + tile * y)
                            }
                            close()
                        }, paint)
                    }
                }
                text(attribute.value.toString(), left + tile / 2, top + 142f, 88f,
                    when (attribute.value) { 20 -> MedievalColors.MaximumGold; 0 -> MedievalColors.RuinStone; else -> MedievalColors.Paper },
                    bold = true, centered = true)
                if (attribute.value == 0) {
                    val bounds = Rect()
                    paint.getTextBounds("0", 0, 1, bounds)
                    val numberLeft = left + tile / 2 - paint.measureText("0") / 2 + bounds.left
                    val numberTop = top + 142f + bounds.top
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = bounds.width() * .1f
                    paint.color = MedievalColors.FilledCard
                    canvas.drawPath(Path().apply {
                        RuinMark.fracture.forEachIndexed { position, (x, y) ->
                            if (position == 0) moveTo(numberLeft + bounds.width() * x, numberTop + bounds.height() * y)
                            else lineTo(numberLeft + bounds.width() * x, numberTop + bounds.height() * y)
                        }
                    }, paint)
                }
                text(attribute.name, left + tile / 2, top + tile + 46f, 30f, white, centered = true)
            }
        }
        text("Imagine a história por trás dos números.", 540f, 1200f, 30f, muted, centered = true)
    }
}
