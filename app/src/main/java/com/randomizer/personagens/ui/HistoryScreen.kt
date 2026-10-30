package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.game.displayName
import com.randomizer.personagens.history.SavedCharacter
import java.text.DateFormat
import java.util.Date

@Composable
internal fun HistoryScreen(
    characters: List<SavedCharacter>, enabled: Boolean,
    onBack: () -> Unit, onOpen: (SavedCharacter) -> Unit,
    onShare: (SavedCharacter) -> Unit, onDelete: (SavedCharacter) -> Unit,
) {
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    Column(Modifier.widthIn(max = 640.dp).fillMaxSize()) {
        Column(Modifier.weight(1f).fillMaxWidth()
            .testTag("history-content").verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Histórico de personagens", style = MaterialTheme.typography.headlineMedium)
            Text("Deslize para a direita para compartilhar ou para a esquerda para excluir.",
                style = MaterialTheme.typography.bodyMedium)
            if (characters.isEmpty()) Text("Nenhum personagem salvo ainda.")
            characters.forEach { saved ->
                key(saved.id) {
                    HistoryItem(saved, enabled && pendingDeleteId == null,
                        { onOpen(saved) }, { onShare(saved) }, { pendingDeleteId = saved.id })
                }
            }
        }
        FixedFooter("history-footer") { BackButton(onClick = onBack, enabled = enabled) }
    }
    characters.firstOrNull { it.id == pendingDeleteId }?.let { saved ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Excluir personagem?") },
            text = { Text("Excluir ${saved.name.ifBlank { saved.game.mode.displayName }} do histórico? Esta ação não pode ser desfeita.") },
            confirmButton = { TextButton(enabled = enabled, onClick = {
                pendingDeleteId = null
                onDelete(saved)
            }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun HistoryItem(
    saved: SavedCharacter, enabled: Boolean,
    onOpen: () -> Unit, onShare: () -> Unit, onDelete: () -> Unit,
) {
    val swipe = rememberSwipeToDismissBoxState(positionalThreshold = { it * 0.35f })
    val share by rememberUpdatedState(onShare)
    val delete by rememberUpdatedState(onDelete)
    LaunchedEffect(swipe) {
        snapshotFlow { swipe.settledValue }.collect { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                // Reposiciona antes de abrir o seletor ou o diálogo. O efeito não é
                // reiniciado pela mudança de valor durante reset().
                swipe.reset()
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> share()
                    SwipeToDismissBoxValue.EndToStart -> delete()
                    SwipeToDismissBoxValue.Settled -> Unit
                }
            }
        }
    }
    SwipeToDismissBox(state = swipe,
        modifier = Modifier.testTag("history-item-${saved.id}"),
        enableDismissFromStartToEnd = enabled, enableDismissFromEndToStart = enabled,
        backgroundContent = {
            val deleting = swipe.dismissDirection == SwipeToDismissBoxValue.EndToStart
            Surface(modifier = Modifier.fillMaxSize().clearAndSetSemantics {}, shape = MaterialTheme.shapes.medium,
                color = if (deleting) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (deleting) Arrangement.End else Arrangement.Start) {
                    Text(if (deleting) "← Excluir" else "Compartilhar →")
                }
            }
        }) {
        Card(onClick = onOpen, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(saved.name.ifBlank { saved.game.mode.displayName }, style = MaterialTheme.typography.titleLarge)
                Text(saved.game.mode.displayName + " · " +
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(saved.createdAt)))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Compartilhar →", style = MaterialTheme.typography.labelMedium)
                    Text("← Excluir", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
