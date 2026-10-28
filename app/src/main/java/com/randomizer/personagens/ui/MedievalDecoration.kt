package com.randomizer.personagens.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.design.MedievalColors

/** Ornamentos vetoriais: disponíveis offline e sem imagens ou fontes externas. */
@Composable
internal fun MedievalBackdrop(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(
            Color(MedievalColors.AgedPaper), Color(MedievalColors.Parchment),
            Color(MedievalColors.Paper), Color(MedievalColors.Parchment), Color(MedievalColors.AgedPaper),
        ))).drawWithCache {
            val speckles = List(180) { index ->
                Offset(((index * 73 + 17) % 997) / 997f * size.width,
                    ((index * 137 + 41) % 991) / 991f * size.height)
            }
            onDrawBehind {
                speckles.forEach { drawCircle(Color(MedievalColors.Bronze).copy(alpha = 0.1f), 0.6.dp.toPx(), it) }
                val inset = 7.dp.toPx()
                drawRect(Color(MedievalColors.Bronze).copy(alpha = 0.6f), Offset(inset, inset),
                    Size((size.width - inset * 2).coerceAtLeast(0f), (size.height - inset * 2).coerceAtLeast(0f)),
                    style = Stroke(1.dp.toPx()))
            }
        },
        content = content,
    )
}

internal fun Modifier.manuscriptFrame(): Modifier = drawWithContent {
    drawContent()
    val color = Color(MedievalColors.Bronze).copy(alpha = 0.7f)
    val inset = 6.dp.toPx()
    val length = 8.dp.toPx()
    for (x in listOf(inset, size.width - inset)) {
        for (y in listOf(inset, size.height - inset)) {
            val dx = if (x < size.width / 2) length else -length
            val dy = if (y < size.height / 2) length else -length
            drawLine(color, Offset(x, y), Offset(x + dx, y), 1.dp.toPx())
            drawLine(color, Offset(x, y), Offset(x, y + dy), 1.dp.toPx())
        }
    }
}

internal fun Modifier.sealRim(): Modifier = drawWithContent {
    drawContent()
    drawCircle(Color(MedievalColors.Gold), (size.minDimension / 2 - 6.dp.toPx()).coerceAtLeast(0f),
        style = Stroke(1.dp.toPx()))
}

@Composable
internal fun HeraldicDivider(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val color = Color(MedievalColors.Bronze)
        val mid = size.width / 2
        val cy = size.height / 2
        val radius = 4.dp.toPx()
        drawLine(color, Offset(0f, cy), Offset(mid - radius * 3, cy), 1.dp.toPx())
        drawLine(color, Offset(mid + radius * 3, cy), Offset(size.width, cy), 1.dp.toPx())
        drawPath(Path().apply {
            moveTo(mid, cy - radius); lineTo(mid + radius, cy)
            lineTo(mid, cy + radius); lineTo(mid - radius, cy); close()
        }, color)
    }
}

@Composable
internal fun HeraldicCrest(modifier: Modifier = Modifier) {
    Canvas(modifier.size(64.dp)) {
        val w = size.width
        val h = size.height
        val shield = Path().apply {
            moveTo(w * 0.15f, h * 0.1f); lineTo(w * 0.85f, h * 0.1f)
            lineTo(w * 0.82f, h * 0.6f); quadraticTo(w * 0.75f, h * 0.82f, w * 0.5f, h * 0.95f)
            quadraticTo(w * 0.25f, h * 0.82f, w * 0.18f, h * 0.6f); close()
        }
        drawPath(shield, Color(MedievalColors.Wine))
        drawPath(shield, Color(MedievalColors.Bronze), style = Stroke(2.dp.toPx()))
        val diamond = Path().apply {
            moveTo(w * 0.5f, h * 0.24f); lineTo(w * 0.69f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.73f); lineTo(w * 0.31f, h * 0.48f); close()
        }
        drawPath(diamond, Color(MedievalColors.Gold), style = Stroke(2.dp.toPx()))
        drawLine(Color(MedievalColors.Gold), Offset(w * 0.31f, h * 0.48f),
            Offset(w * 0.69f, h * 0.48f), 1.dp.toPx())
    }
}
