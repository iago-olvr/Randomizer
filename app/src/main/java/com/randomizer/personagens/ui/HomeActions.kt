package com.randomizer.personagens.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randomizer.personagens.design.MedievalColors

@Composable
internal fun HomeHeading() {
    Text("Um personagem.\nMuitas histórias.",
        style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary)
}

/** Rodapé fixo: as opções podem rolar sem esconder as três ações principais. */
@Composable
internal fun HomeActions(enabled: Boolean, onHelp: () -> Unit, onStart: () -> Unit, onHistory: () -> Unit) {
    Box {
        Row(Modifier.fillMaxWidth().testTag("home-actions").padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
            val labels = listOf("Como jogar", "Criar personagem", "Histótico")
            val actions = listOf(onHelp, onStart, onHistory)
            labels.forEachIndexed { index, label ->
                Column(Modifier.weight(1f).clickable(enabled = enabled, role = Role.Button, onClick = actions[index]),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = CircleShape,
                        color = Color(MedievalColors.Wine),
                        contentColor = Color(MedievalColors.Paper),
                        shadowElevation = if (index == 1) 10.dp else 3.dp,
                        modifier = Modifier.size(if (index == 1) 80.dp else 60.dp)
                            .testTag("home-icon-$index").sealRim()) {
                        Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                            when (index) {
                                0 -> Text("?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                1 -> Text("+", fontSize = 42.sp)
                                else -> HistoryIcon()
                            }
                        }
                    }
                    Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun HistoryIcon() {
    val color = Color(MedievalColors.Paper)
    Canvas(Modifier.size(28.dp)) {
        val stroke = 2.dp.toPx()
        drawCircle(color, size.minDimension * 0.4f, style = Stroke(stroke))
        drawLine(color, center, Offset(center.x, size.height * 0.25f), stroke, StrokeCap.Round)
        drawLine(color, center, Offset(size.width * 0.7f, size.height * 0.6f), stroke, StrokeCap.Round)
    }
}

@Composable
internal fun HistorySaveStatus(saving: Boolean, saved: Boolean) {
    Text(if (saving) "Salvando no histórico…" else if (saved) "Salvo no histórico local"
        else "Personagem ainda não salvo no histórico",
        modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall)
}
