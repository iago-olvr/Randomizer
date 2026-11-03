package com.randomizer.personagens.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CharacterRules
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.game.Stage
import com.randomizer.personagens.game.displayName
import com.randomizer.personagens.design.MedievalColors

/** Duas fileiras de cartões e uma ação central, seguindo o tabuleiro de referência. */
@Composable
internal fun CharacterBoard(
    game: GameState,
    isRolling: Boolean,
    onRoll: () -> Unit,
    onChange: (GameState) -> Unit,
    onCancel: () -> Unit,
    onExport: () -> Unit,
    characterName: String = "",
    onEditName: () -> Unit = {},
    onHistory: () -> Unit = {},
    resultStatus: @Composable () -> Unit = {},
) {
    var detailIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    Column(Modifier.widthIn(max = 640.dp).fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("board-content")
                .verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(game.mode.displayName, color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = onCancel, enabled = !isRolling, shape = CircleShape,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = "Cancelar" },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text("X", fontSize = 26.sp)
                }
            }
            StageInstruction(game, characterName)
            AttributeRow(game, 0, isRolling, onChange) { detailIndex = it }
            AttributeRow(game, 1, isRolling, onChange) { detailIndex = it }
        }
        FixedFooter("board-footer") {
            BoardAction(game, isRolling, onRoll, onChange)
            if (game.stage == Stage.COMPLETE) {
                ResultActions(!isRolling, onEditName, onExport, onHistory)
                resultStatus()
            }
        }
    }
    detailIndex?.let { index ->
        val attribute = CharacterRules.attributes[index]
        AlertDialog(
            onDismissRequest = { detailIndex = null },
            title = { Text(attribute.name) },
            text = { Text(requireNotNull(attribute.extremes)) },
            confirmButton = { TextButton(onClick = { detailIndex = null }) { Text("Entendi") } },
        )
    }
}

@Composable
private fun AttributeRow(
    game: GameState,
    group: Int,
    isRolling: Boolean,
    onChange: (GameState) -> Unit,
    onDetails: (Int) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Grupo ${group + 1}", color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium)
            HeraldicDivider(Modifier.weight(1f).padding(start = 12.dp))
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val tileWidth = (maxWidth - 24.dp) / 4
            Row(Modifier.fillMaxWidth(), horizontalArrangement = if (group == 0)
                Arrangement.spacedBy(8.dp) else Arrangement.SpaceEvenly) {
                CharacterRules.groups[group].forEach { index ->
                    val attribute = CharacterRules.attributes[index]
                    val chooseMaximum = game.stage == Stage.CHOOSE_MAXIMUM && game.group == group
                    val nextRoll = game.stage == Stage.ROLL_ATTRIBUTE && game.group == group &&
                        game.remaining.first() == index
                    val highlighted = chooseMaximum || nextRoll
                    val value = game.values[index]
                    Column(
                        modifier = Modifier.width(tileWidth).clickable(
                            enabled = !isRolling && (chooseMaximum || attribute.extremes != null),
                            onClickLabel = if (chooseMaximum) "Maximizar ${attribute.name}" else "Ver referências",
                            onClick = {
                                if (chooseMaximum) onChange(CharacterGenerator.chooseMaximum(game, index))
                                else onDetails(index)
                            },
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f).testTag("attribute-$index")
                                .manuscriptFrame().attributeFinish(value).attributeSparkles(value).semantics {
                                    contentDescription = "${attribute.name}: ${value ?: "não definido"}" +
                                        when (value) { 20 -> ", máximo dourado"; 0 -> ", ruína"; else -> "" }
                                },
                            shape = RoundedCornerShape(4.dp),
                            color = when {
                                value != null -> Color(MedievalColors.FilledCard)
                                highlighted -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(if (value == 20 || highlighted) 2.dp else 1.dp,
                                if (value == 20) AttributeGold else if (highlighted) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val valueStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                                Text(value?.toString() ?: "—", style = valueStyle,
                                    modifier = Modifier.fracturedValue(value),
                                    color = when (value) {
                                        20 -> AttributeGold
                                        0 -> Color(MedievalColors.RuinStone)
                                        null -> MaterialTheme.colorScheme.onSurface
                                        else -> Color(MedievalColors.Paper)
                                    })
                            }
                        }
                        Text(attribute.name, textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium)
                        if (attribute.extremes != null) {
                            OutlinedButton(onClick = { onDetails(index) }, enabled = !isRolling,
                                shape = CircleShape,
                                modifier = Modifier.size(48.dp).testTag("hint-$index")
                                    .semantics { contentDescription = "Ver dica de ${attribute.name}" },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                                Text("?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageInstruction(game: GameState, characterName: String) {
    Column(Modifier.fillMaxWidth().testTag("stage-instruction"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val instruction = when (game.stage) {
            Stage.CHOOSE_MAXIMUM -> "Toque em um atributo do grupo ${game.group + 1} para receber 20"
            Stage.ROLL_MINIMUM -> "Qual atributo receberá 0?"
            Stage.ROLL_ATTRIBUTE -> "D20 para ${CharacterRules.attributes[game.remaining.first()].name}"
            Stage.COMPLETE -> characterName.ifBlank { "Imaginem juntos a personalidade e a história desse personagem." }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().manuscriptFrame(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        ) {
            Text(instruction, Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
        if (game.stage == Stage.ROLL_MINIMUM) {
            val mapping = if (game.group == 0) {
                game.remaining.mapIndexed { position, index ->
                    "${position + 1} → ${CharacterRules.attributes[index].name}"
                }.joinToString(" · ") + "\n4 → rolar novamente"
            } else {
                "1 ou 3 → ${CharacterRules.attributes[game.remaining[0]].name}\n" +
                    "2 ou 4 → ${CharacterRules.attributes[game.remaining[1]].name}"
            }
            Text(mapping, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BoardAction(
    game: GameState,
    isRolling: Boolean,
    onRoll: () -> Unit,
    onChange: (GameState) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (game.stage != Stage.COMPLETE && game.lastEvent.isNotBlank()) {
            Text(game.lastEvent, Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary)
        }
        if (game.stage == Stage.COMPLETE) return@Column
        if (game.source == DiceSource.PHYSICAL && game.dieSides > 0) {
            PhysicalResultInput(game, !isRolling, onChange)
        } else {
            CircularAction(
                label = if (game.dieSides > 0) "Rolar D${game.dieSides}" else "Escolha\num 20",
                enabled = !isRolling && game.dieSides > 0,
                onClick = onRoll,
            )
        }
    }
}

@Composable
private fun ResultActions(enabled: Boolean, onEditName: () -> Unit, onShare: () -> Unit, onHistory: () -> Unit) {
    Row(Modifier.fillMaxWidth().testTag("result-actions"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Editar nome" to onEditName, "Compartilhar" to onShare, "Ver histórico" to onHistory)
            .forEach { (label, action) ->
                Button(onClick = action, enabled = enabled,
                    modifier = Modifier.weight(1f).manuscriptFrame(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 12.dp)) {
                    Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                }
            }
    }
}

@Composable
private fun CircularAction(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, shape = CircleShape,
        modifier = Modifier.size(88.dp).testTag("board-action")
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape).sealRim(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)) {
        Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun PhysicalResultInput(game: GameState, enabled: Boolean, onChange: (GameState) -> Unit) {
    var input by rememberSaveable(game.group, game.stage, game.filledCount) { mutableStateOf("") }
    val result = input.toIntOrNull()
    val valid = result != null && result in 1..game.dieSides
    OutlinedTextField(
        enabled = enabled,
        value = input,
        onValueChange = { if (it.length <= 2 && it.all { digit -> digit in '0'..'9' }) input = it },
        label = { Text("Resultado do D${game.dieSides}") },
        supportingText = { Text("Digite um número de 1 a ${game.dieSides}.") },
        isError = input.isNotEmpty() && !valid,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.widthIn(max = 280.dp).fillMaxWidth(),
    )
    Button(enabled = enabled && valid, onClick = {
        if (valid) {
            input = ""
            onChange(CharacterGenerator.enterPhysicalResult(game, requireNotNull(result)))
        }
    }) { Text("Confirmar resultado") }
}
