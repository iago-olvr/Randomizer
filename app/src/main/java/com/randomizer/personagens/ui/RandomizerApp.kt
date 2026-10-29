package com.randomizer.personagens.ui

import androidx.activity.compose.BackHandler
import android.content.Intent
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import com.randomizer.personagens.history.CharacterHistory
import com.randomizer.personagens.history.SavedCharacter
import com.randomizer.personagens.export.CharacterSharing
import java.io.File
import java.util.UUID
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.game.Stage
import com.randomizer.personagens.game.displayName
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

private enum class AppPage { SETUP, BOARD, RESULT, HISTORY, HELP }
private data class PageSnapshot(val page: AppPage, val game: GameState, val name: String)

private val GameStateSaver = listSaver<GameState, String>(
    save = { state ->
        listOf(state.mode.name, state.source.name, state.group.toString(), state.stage.name,
            state.values.joinToString(",") { it?.toString() ?: "-" }, state.lastEvent)
    },
    restore = { saved ->
        GameState(
            mode = CreationMode.valueOf(saved[0]),
            source = DiceSource.valueOf(saved[1]),
            group = saved[2].toInt(),
            stage = Stage.valueOf(saved[3]),
            values = saved[4].split(",").map { it.toIntOrNull() },
            lastEvent = saved[5],
        )
    },
)

@Composable
fun RandomizerApp() {
    var mode by rememberSaveable { mutableStateOf(CreationMode.EXTREMES) }
    var source by rememberSaveable { mutableStateOf(DiceSource.APP) }
    var started by rememberSaveable { mutableStateOf(false) }
    var game by rememberSaveable(stateSaver = GameStateSaver) {
        mutableStateOf(CharacterGenerator.start(mode, source))
    }
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    // O resultado é sorteado uma vez e só aplicado após a revelação do dado.
    // Salvar o valor evita sortear outro resultado se a tela for recriada.
    var rollingResult by rememberSaveable { mutableStateOf<Int?>(null) }
    var exporting by remember { mutableStateOf(false) }
    var deletingHistory by remember { mutableStateOf(false) }
    var exportMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val history = remember(context) { CharacterHistory(File(context.filesDir, "characters.json")) }
    var characters by remember { mutableStateOf<List<SavedCharacter>>(emptyList()) }
    var showHistory by rememberSaveable { mutableStateOf(false) }
    var showHelp by rememberSaveable { mutableStateOf(false) }
    var characterId by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }
    var createdAt by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var characterName by rememberSaveable { mutableStateOf("") }
    var editingName by rememberSaveable { mutableStateOf(false) }
    var nameDraft by rememberSaveable { mutableStateOf("") }
    var savingHistory by remember { mutableStateOf(false) }
    var historySaved by remember { mutableStateOf(false) }
    var saveAttempt by remember { mutableStateOf(0) }
    LaunchedEffect(showHistory) {
        try {
            characters = withContext(Dispatchers.IO) { history.load() }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            exportMessage = "Não foi possível ler o histórico local. Os dados existentes foram preservados."
        }
    }
    LaunchedEffect(started, game, characterId, characterName, saveAttempt) {
        historySaved = false
        if (started && game.stage == Stage.COMPLETE) {
            savingHistory = true
            try {
                val saved = SavedCharacter(characterId, createdAt, characterName, game)
                characters = withContext(Dispatchers.IO) { history.save(saved) }
                historySaved = true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                exportMessage = "Não foi possível salvar no histórico. Tente novamente antes de sair."
            } finally {
                savingHistory = false
            }
        }
    }
    fun shareCharacter(sharedGame: GameState, sharedName: String) {
        if (exporting || deletingHistory || savingHistory) return
        exporting = true
        scope.launch {
            try {
                val intent = withContext(Dispatchers.IO) {
                    CharacterSharing.prepare(context, sharedGame, sharedName)
                }
                context.startActivity(Intent.createChooser(intent, "Compartilhar personagem"))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                exportMessage = "Não foi possível compartilhar a imagem. Tente novamente."
            } finally {
                exporting = false
            }
        }
    }
    BackHandler(enabled = showHelp || showHistory || started) {
        if (rollingResult != null || exporting || savingHistory || deletingHistory) return@BackHandler
        if (showHelp) showHelp = false
        else if (showHistory) showHistory = false
        else if (game.stage == Stage.COMPLETE && historySaved) started = false
        else confirmReset = true
    }
    val currentPage = when {
        showHelp -> AppPage.HELP
        showHistory -> AppPage.HISTORY
        !started -> AppPage.SETUP
        game.stage == Stage.COMPLETE -> AppPage.RESULT
        else -> AppPage.BOARD
    }

    RandomizerTheme {
        Scaffold { insets ->
            MedievalBackdrop {
                AnimatedContent(
                    targetState = PageSnapshot(currentPage, game, characterName),
                    contentKey = { it.page },
                    modifier = Modifier.fillMaxSize().padding(insets).imePadding().clipToBounds(),
                    transitionSpec = {
                        val direction = if (targetState.page == AppPage.SETUP) -1 else 1
                        (fadeIn(tween(320)) + slideInHorizontally(tween(380)) { direction * it / 10 }) togetherWith
                            (fadeOut(tween(180)) + slideOutHorizontally(tween(300)) { -direction * it / 12 })
                    },
                    label = "Folhear páginas",
                ) { snapshot ->
                    val activePage = snapshot.page == currentPage
                    Box(Modifier.fillMaxSize().testTag("page-${snapshot.page.name.lowercase()}")
                        .then(if (activePage) Modifier else Modifier.clearAndSetSemantics {}),
                        contentAlignment = Alignment.TopCenter) {
                        if (snapshot.page == AppPage.HELP) {
                            HelpScreen(onBack = { showHelp = false }, enabled = activePage)
                        } else if (snapshot.page == AppPage.HISTORY) {
                            HistoryScreen(characters,
                                enabled = activePage && !exporting && !savingHistory && !deletingHistory,
                                onBack = { showHistory = false },
                                onOpen = { saved ->
                                    characterId = saved.id
                                    createdAt = saved.createdAt
                                    characterName = saved.name
                                    game = saved.game
                                    started = true
                                    showHistory = false
                                },
                                onShare = { saved -> shareCharacter(saved.game, saved.name) },
                                onDelete = { saved ->
                                    if (!deletingHistory && !exporting && !savingHistory) {
                                        deletingHistory = true
                                        scope.launch {
                                            try {
                                                val remaining = withContext(Dispatchers.IO) { history.delete(saved.id) }
                                                // A ficha aberta não deve recriar automaticamente um registro excluído.
                                                if (started && characterId == saved.id) started = false
                                                characters = remaining
                                            } catch (cancelled: CancellationException) {
                                                throw cancelled
                                            } catch (_: Exception) {
                                                exportMessage = "Não foi possível excluir o personagem. O registro foi preservado. Tente novamente."
                                            } finally {
                                                deletingHistory = false
                                            }
                                        }
                                    }
                                },
                            )
                        } else if (snapshot.page != AppPage.SETUP) {
                            CharacterBoard(
                                game = snapshot.game,
                                isRolling = rollingResult != null || exporting || savingHistory || !activePage,
                                onRoll = {
                                    if (rollingResult == null) {
                                        rollingResult = Random.nextInt(1, game.dieSides + 1)
                                    }
                                },
                                onChange = { if (activePage) game = it },
                                onCancel = {
                                    started = false
                                    confirmReset = false
                                },
                                onExport = { shareCharacter(game, characterName) },
                                characterName = snapshot.name,
                                onEditName = {
                                    nameDraft = characterName
                                    editingName = true
                                },
                                onHistory = { if (historySaved) showHistory = true else saveAttempt++ },
                                resultStatus = {
                                    HistorySaveStatus(savingHistory, historySaved)
                                },
                            )
                        } else {
                            Column(Modifier.widthIn(max = 640.dp).fillMaxSize()) {
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                        .verticalScroll(rememberScrollState()).padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    HeraldicCrest(Modifier.align(Alignment.CenterHorizontally))
                                    HomeHeading()
                                    HeraldicDivider()
                                    SetupScreen(mode, source, { mode = it }, { source = it }, activePage)
                                }
                                HomeActions(enabled = activePage,
                                    onHelp = { showHelp = true },
                                    onStart = {
                                        if (!started) {
                                            characterId = UUID.randomUUID().toString()
                                            createdAt = System.currentTimeMillis()
                                            characterName = ""
                                            game = CharacterGenerator.start(mode, source)
                                            started = true
                                        }
                                    },
                                    onHistory = { showHistory = true },
                                )
                            }
                        }
                    }
                }
            }
        }
        rollingResult?.let { result ->
            DiceRollDialog(sides = game.dieSides, result = result) {
                game = CharacterGenerator.roll(game) { result }
                rollingResult = null
            }
        }
        if (editingName) {
            AlertDialog(
                onDismissRequest = { editingName = false },
                title = { Text("Nome do personagem") },
                text = {
                    OutlinedTextField(value = nameDraft,
                        onValueChange = { if (it.length <= 60) nameDraft = it },
                        label = { Text("Nome (opcional)") }, singleLine = true,
                        supportingText = { Text("Até 60 caracteres. Aparece também na imagem.") })
                },
                confirmButton = { TextButton(onClick = {
                    characterName = nameDraft.trim()
                    editingName = false
                }) { Text("Salvar nome") } },
                dismissButton = { TextButton(onClick = { editingName = false }) { Text("Cancelar") } },
            )
        }
        if (confirmReset) {
            AlertDialog(
                onDismissRequest = { confirmReset = false },
                title = { Text("Começar outro personagem?") },
                text = { Text("Os valores deste personagem serão descartados.") },
                confirmButton = {
                    TextButton(onClick = { started = false; confirmReset = false }) { Text("Recomeçar") }
                },
                dismissButton = {
                    TextButton(onClick = { confirmReset = false }) { Text("Continuar personagem") }
                },
            )
        }
        exportMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { exportMessage = null },
                title = { Text("Personagem") },
                text = { Text(message) },
                confirmButton = {
                    val retry = started && game.stage == Stage.COMPLETE && !historySaved
                    TextButton(onClick = {
                        exportMessage = null
                        if (retry) saveAttempt++
                    }) { Text(if (retry) "Tentar salvar novamente" else "Entendi") }
                },
            )
        }
    }
}

@Composable
private fun SetupScreen(
    mode: CreationMode,
    source: DiceSource,
    onMode: (CreationMode) -> Unit,
    onSource: (DiceSource) -> Unit,
    enabled: Boolean,
) {
    Text("Tipo de Personagem", style = MaterialTheme.typography.titleMedium)
    ChoiceCard(CreationMode.EXTREMES.displayName, "Escolha um 20 em cada grupo. O D4 define um 0 e o D20 preenche os demais.",
        mode == CreationMode.EXTREMES, enabled) { onMode(CreationMode.EXTREMES) }
    ChoiceCard("Vilão", "Tudo ao acaso: um D20 para cada um dos sete atributos.",
        mode == CreationMode.VILLAIN, enabled) { onMode(CreationMode.VILLAIN) }
    Text("Quais dados usar?", style = MaterialTheme.typography.titleMedium)
    ChoiceCard("Dados no aplicativo", "Toque para rolar a cada etapa.", source == DiceSource.APP, enabled) {
        onSource(DiceSource.APP)
    }
    ChoiceCard("Dados físicos", "Lance seus dados e digite o resultado de cada rolagem.", source == DiceSource.PHYSICAL, enabled) {
        onSource(DiceSource.PHYSICAL)
    }

}

@Composable
private fun ChoiceCard(title: String, description: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().manuscriptFrame(),
        border = BorderStroke(if (selected) 2.dp else 1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = if (selected)
            MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = null)
            Column(Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RandomizerPreview() {
    RandomizerApp()
}
