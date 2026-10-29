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
    Column(Modifier.widthIn(max = 640.dp).fillMaxWidth()
        .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Histórico de personagens", style = MaterialTheme.typography.headlineMedium)
        BackButton(onClick = onBack, enabled = enabled)
        Text("Deslize para a direita para compartilhar ou para a esquerda para excluir.",
            style = MaterialTheme.typography.bodyMedium)
        if (characters.isEmpty()) Text("Nenhum personagem salvo ainda.")
        characters.forEach { saved ->
            key(saved.id) {
                HistoryItem(saved, enabled, { onOpen(saved) }, { onShare(saved) }, { onDelete(saved) })
            }
        }
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
    LaunchedEffect(swipe.currentValue) {
        when (swipe.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> share()
            SwipeToDismissBoxValue.EndToStart -> delete()
            SwipeToDismissBoxValue.Settled -> return@LaunchedEffect
        }
        // Compartilhar não remove o registro; a próxima interação começa no centro.
        swipe.reset()
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
                    TextButton(onClick = onShare, enabled = enabled) { Text("Compartilhar →") }
                    TextButton(onClick = onDelete, enabled = enabled) { Text("← Excluir") }
                }
            }
        }
    }
}
