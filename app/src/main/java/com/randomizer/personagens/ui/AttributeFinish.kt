package com.randomizer.personagens.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.design.MedievalColors
import com.randomizer.personagens.design.RuinMark

internal val AttributeGold = Color(MedievalColors.MaximumGold)

/** Os ornamentos ficam nas bordas para preservar a leitura do número. */
internal fun Modifier.attributeFinish(value: Int?): Modifier = drawWithContent {
    drawContent()
    if (value == 20) {
        drawRect(Brush.linearGradient(listOf(AttributeGold, Color(0xFFFFE8A0), AttributeGold)),
            topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
            size = Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()),
            style = Stroke(2.dp.toPx()))
        val gold = Color(0xFFFFE8A0)
        listOf(Offset(size.width * .15f, size.height * .15f),
            Offset(size.width * .85f, size.height * .85f)).forEach { point ->
            drawCircle(AttributeGold.copy(alpha = .18f), 9.dp.toPx(), point)
            drawLine(gold, point - Offset(5.dp.toPx(), 0f), point + Offset(5.dp.toPx(), 0f), 1.5.dp.toPx())
            drawLine(gold, point - Offset(0f, 5.dp.toPx()), point + Offset(0f, 5.dp.toPx()), 1.5.dp.toPx())
        }
    } else if (value == 0) {
        val crack = Path().apply {
            moveTo(size.width * .22f, 0f)
            lineTo(size.width * .18f, size.height * .14f)
            lineTo(size.width * .29f, size.height * .23f)
            lineTo(size.width * .23f, size.height * .36f)
            moveTo(size.width * .29f, size.height * .23f)
            lineTo(size.width * .42f, size.height * .18f)
            moveTo(size.width, size.height * .62f)
            lineTo(size.width * .82f, size.height * .69f)
            lineTo(size.width * .88f, size.height * .81f)
            lineTo(size.width * .72f, size.height)
        }
        drawPath(crack, Color(0xFF67594B), style = Stroke(2.dp.toPx()))
        RuinMark.fragments.forEach { points ->
            drawPath(Path().apply {
                points.forEachIndexed { index, (x, y) ->
                    if (index == 0) moveTo(size.width * x, size.height * y)
                    else lineTo(size.width * x, size.height * y)
                }
                close()
            }, Color(MedievalColors.RuinStone))
        }
    }
}

/** A quebra é desenhada somente sobre o número, preservando sua leitura. */
internal fun Modifier.fracturedValue(value: Int?): Modifier = if (value != 0) this else drawWithContent {
    drawContent()
    val crack = Path().apply {
        RuinMark.fracture.forEachIndexed { index, (x, y) ->
            if (index == 0) moveTo(size.width * x, size.height * y)
            else lineTo(size.width * x, size.height * y)
        }
    }
    drawPath(crack, Color(MedievalColors.FilledCard), style = Stroke(size.width * .1f))
}
