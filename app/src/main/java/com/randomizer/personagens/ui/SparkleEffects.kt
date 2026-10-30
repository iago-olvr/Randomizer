package com.randomizer.personagens.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Só mantém uma animação ativa enquanto o cartão contém 20. */
@Composable
internal fun Modifier.attributeSparkles(value: Int?): Modifier {
    if (value != 20) return this
    val transition = rememberInfiniteTransition(label = "Brilhos do atributo máximo")
    val phase = transition.animateFloat(0f, 1f,
        infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "Partículas douradas")
    return drawWithContent {
        drawContent()
        repeat(12) { index ->
            val age = (phase.value + index / 12f) % 1f
            val edge = if (index % 2 == 0) .12f else .88f
            val x = size.width * edge + sin(age * PI * 2 + index).toFloat() * 5.dp.toPx()
            val y = size.height * (.9f - age) - age * 10.dp.toPx()
            goldenSparkle(Offset(x, y), (2f + index % 3).dp.toPx(), sin(age * PI).toFloat())
        }
    }
}

/** Explosão finita, disparada somente depois de revelar um 20 no D20. */
@Composable
internal fun CriticalSuccessEffect(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(1300, easing = LinearEasing))
    }
    Canvas(modifier) {
        val fraction = progress.value
        val alpha = sin(fraction * PI).toFloat().coerceIn(0f, 1f)
        drawCircle(AttributeGold.copy(alpha = alpha * .16f), size.minDimension * .46f)
        repeat(28) { index ->
            val angle = index * PI * 2 / 28
            val radius = size.minDimension * (.18f + fraction * .3f) * (if (index % 2 == 0) 1f else .8f)
            val point = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius
            goldenSparkle(point, (3f + index % 3).dp.toPx(), alpha)
        }
    }
}

private fun DrawScope.goldenSparkle(point: Offset, radius: Float, alpha: Float) {
    drawCircle(AttributeGold.copy(alpha = alpha * .22f), radius * 2, point)
    val color = Color(0xFFFFE8A0).copy(alpha = alpha.coerceIn(0f, 1f))
    drawLine(AttributeGold.copy(alpha = alpha), point - Offset(radius, 0f),
        point + Offset(radius, 0f), 2.dp.toPx())
    drawLine(color, point - Offset(0f, radius), point + Offset(0f, radius), 1.5.dp.toPx())
    drawCircle(color, radius * .3f, point)
}
