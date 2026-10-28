package com.randomizer.personagens.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ActivityNotFoundException
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.game.Stage
import com.randomizer.personagens.game.displayName
import com.randomizer.personagens.export.CharacterImageExporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.random.Random

private enum class AppPage { SETUP, BOARD, RESULT }
private data class PageSnapshot(val page: AppPage, val game: GameState)

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
    var exportSnapshot by rememberSaveable(stateSaver = GameStateSaver) { mutableStateOf(game) }
    var exporting by remember { mutableStateOf(false) }
    var exportMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri == null) {
            exporting = false
        } else {
            val snapshot = exportSnapshot
            exporting = true
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val output = context.contentResolver.openOutputStream(uri, "w")
                            ?: throw IOException("Não foi possível abrir o destino.")
                        output.use { CharacterImageExporter.writePng(snapshot, it) }
                    }
                    exportMessage = "Imagem salva no local escolhido."
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    exportMessage = "Não foi possível salvar a imagem. Escolha outro local e tente novamente."
                } finally {
                    exporting = false
                }
            }
        }
    }

    BackHandler(enabled = started && rollingResult == null && !exporting) { confirmReset = true }
    val currentPage = when {
        !started -> AppPage.SETUP
        game.stage == Stage.COMPLETE -> AppPage.RESULT
        else -> AppPage.BOARD
    }

    RandomizerTheme {
        Scaffold { insets ->
            MedievalBackdrop {
                AnimatedContent(
                    targetState = PageSnapshot(currentPage, game),
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
                        if (snapshot.page != AppPage.SETUP) {
                            CharacterBoard(
                                game = snapshot.game,
                                isRolling = rollingResult != null || exporting || !activePage,
                                onRoll = {
                                    if (rollingResult == null) {
                                        rollingResult = Random.nextInt(1, game.dieSides + 1)
                                    }
                                },
                                onChange = { if (activePage) game = it },
                                onNew = {
                                    if (game.stage == Stage.COMPLETE) started = false else confirmReset = true
                                },
                                onExport = {
                                    if (!exporting && game.stage == Stage.COMPLETE) {
                                        exportSnapshot = game
                                        exporting = true
                                        try {
                                            val name = if (game.mode == CreationMode.VILLAIN) "vilao" else "personagem"
                                            exportLauncher.launch("randomizer-$name-${System.currentTimeMillis()}.png")
                                        } catch (_: ActivityNotFoundException) {
                                            exporting = false
                                            exportMessage = "Não foi encontrado um aplicativo para escolher onde salvar a imagem."
                                        }
                                    }
                                },
                            )
                        } else {
                            Column(
                                modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth()
                                    .verticalScroll(rememberScrollState()).padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text("RANDOMIZER", color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge, letterSpacing = 3.sp)
                                HeraldicCrest(Modifier.align(Alignment.CenterHorizontally))
                                Text("Um personagem.\nMuitas histórias.",
                                    style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                                HeraldicDivider()
                                SetupScreen(mode, source, { mode = it }, { source = it }, activePage) {
                                    if (!started) {
                                        game = CharacterGenerator.start(mode, source)
                                        started = true
                                    }
                                }
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
                title = { Text("Exportação") },
                text = { Text(message) },
                confirmButton = { TextButton(onClick = { exportMessage = null }) { Text("Entendi") } },
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
    onStart: () -> Unit,
) {
    Text("Use os dados para descobrir os atributos. Depois, dê vida ao resultado.",
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text("Como criar?", style = MaterialTheme.typography.titleMedium)
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
    Button(onClick = onStart, enabled = enabled, shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 52.dp).manuscriptFrame()) {
        Text("Criar personagem")
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
