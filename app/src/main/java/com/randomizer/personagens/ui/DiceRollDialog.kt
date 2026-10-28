package com.randomizer.personagens.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/** Animação decorativa: os números intermediários não participam do sorteio. */
@Composable
internal fun DiceRollDialog(sides: Int, result: Int, onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    var revealed by remember { mutableStateOf(false) }
    val finish by rememberUpdatedState(onFinished)

    LaunchedEffect(sides, result) {
        progress.animateTo(1f, tween(durationMillis = 1100, easing = FastOutSlowInEasing))
        revealed = true
        delay(650)
        finish()
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 8.dp,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.manuscriptFrame()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(if (revealed) "Resultado do D$sides" else "Rolando D$sides…",
                    style = MaterialTheme.typography.titleLarge)
                Box(
                    modifier = Modifier.size(200.dp).clearAndSetSemantics {
                        contentDescription = if (revealed) "D$sides: $result" else "D$sides rolando"
                        liveRegion = LiveRegionMode.Polite
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    val value = if (revealed) result else (progress.value * 32).toInt() % sides + 1
                    Box(
                        modifier = Modifier.size(156.dp).graphicsLayer {
                            val fraction = progress.value
                            val wobble = sin(fraction * PI * 6).toFloat() * (1f - fraction)
                            rotationZ = 720f * fraction
                            rotationY = wobble * 30f
                            translationY = -abs(wobble) * 26.dp.toPx()
                            scaleX = 0.85f + fraction * 0.15f
                            scaleY = scaleX
                            cameraDistance = 12f * density
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        DieFace(sides)
                        Text(
                            value.toString(),
                            modifier = Modifier.padding(top = if (sides == 4) 22.dp else 0.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
                Text(if (revealed) "O destino escolheu: $result" else "Que história vem por aí?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun DieFace(sides: Int) {
    val gold = MaterialTheme.colorScheme.primary
    val edge = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.45f)
    Canvas(Modifier.size(156.dp)) {
        val w = size.width
        val h = size.height
        val vertices = if (sides == 4) listOf(
            Offset(w * 0.5f, h * 0.05f),
            Offset(w * 0.95f, h * 0.9f),
            Offset(w * 0.05f, h * 0.9f),
        ) else listOf(
            Offset(w * 0.5f, h * 0.03f),
            Offset(w * 0.92f, h * 0.26f),
            Offset(w * 0.92f, h * 0.74f),
            Offset(w * 0.5f, h * 0.97f),
            Offset(w * 0.08f, h * 0.74f),
            Offset(w * 0.08f, h * 0.26f),
        )
        val outline = Path().apply {
            moveTo(vertices.first().x, vertices.first().y)
            vertices.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        }
        drawPath(outline, Brush.linearGradient(listOf(gold, gold.copy(alpha = 0.75f))))
        drawPath(outline, edge, style = Stroke(2.dp.toPx()))
        if (sides == 20) {
            val face = listOf(Offset(w * 0.5f, h * 0.17f),
                Offset(w * 0.2f, h * 0.71f), Offset(w * 0.8f, h * 0.71f))
            for (index in face.indices) {
                drawLine(edge, face[index], face[(index + 1) % face.size], 1.5.dp.toPx())
            }
            val connections = listOf(0 to 0, 1 to 0, 1 to 2, 2 to 2, 3 to 2, 3 to 1, 4 to 1, 5 to 1, 5 to 0)
            connections.forEach { (outer, inner) ->
                drawLine(edge, vertices[outer], face[inner], 1.dp.toPx())
            }
        } else {
            drawLine(edge, vertices[0], Offset(w * 0.5f, h * 0.22f), 2.dp.toPx())
            drawLine(edge, vertices[1], Offset(w * 0.8f, h * 0.8f), 2.dp.toPx())
            drawLine(edge, vertices[2], Offset(w * 0.2f, h * 0.8f), 2.dp.toPx())
        }
    }
}
