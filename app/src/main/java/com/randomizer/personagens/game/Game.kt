package com.randomizer.personagens.game

import kotlin.random.Random

enum class CreationMode { VILLAIN, EXTREMES }
enum class DiceSource { APP, PHYSICAL }
enum class Stage { CHOOSE_MAXIMUM, ROLL_MINIMUM, ROLL_ATTRIBUTE, COMPLETE }

data class Attribute(val name: String, val extremes: String? = null)

object CharacterRules {
    val attributes = listOf(
        Attribute("Inteligência", "0 -> Pergaminho em branco | 20 -> Biblioteca ambulante"),
        Attribute("Dinheiro", "0 -> Depois de pagar os boletos | 20 -> Antes de pagar os boletos"),
        Attribute("Saúde", "0 -> Se levantar a mão DEUS puxa | 20 -> Data de válidade do Mel"),
        Attribute("Beleza", "0 -> Da medo no espelho | 20 -> Filho do Michael B. Jordan com a Alessandra Negrini"),
        Attribute("Físico", "0 -> Alvo de Bullying | 20 -> Escultura de Michelangelo"),
        Attribute("Altura", "0 -> Desde Fadas Até Anões | 20 -> Minímo 2,10 de altura"),
        Attribute("Cabelo", "0 -> Só o Couro Cabeçudo | 20 -> Propaganda de Xampu"),
    )
    val groups = listOf(0..3, 4..6)
}

/** Estado imutável: cada ação válida produz um novo estado. Não depende do Android. */
data class GameState(
    val mode: CreationMode,
    val source: DiceSource,
    val group: Int = 0,
    val stage: Stage,
    val values: List<Int?> = List(7) { null },
    val lastEvent: String = "",
) {
    val remaining: List<Int>
        get() = CharacterRules.groups[group].filter { values[it] == null }

    val dieSides: Int
        get() = when (stage) {
            Stage.ROLL_MINIMUM -> 4
            Stage.ROLL_ATTRIBUTE -> 20
            else -> 0
        }

    val filledCount: Int get() = values.count { it != null }
}

object CharacterGenerator {
    fun start(mode: CreationMode, source: DiceSource) = GameState(
        mode = mode,
        source = source,
        stage = if (mode == CreationMode.VILLAIN) Stage.ROLL_ATTRIBUTE else Stage.CHOOSE_MAXIMUM,
    )

    fun chooseMaximum(state: GameState, attributeIndex: Int): GameState {
        require(state.stage == Stage.CHOOSE_MAXIMUM) { "Não é a etapa de escolher o máximo." }
        require(attributeIndex in state.remaining) { "Escolha um atributo do grupo atual." }
        return state.copy(
            values = state.values.updated(attributeIndex, 20),
            stage = Stage.ROLL_MINIMUM,
            lastEvent = "${CharacterRules.attributes[attributeIndex].name} recebeu 20.",
        )
    }

    fun roll(state: GameState, random: (Int) -> Int = { Random.nextInt(1, it + 1) }): GameState {
        require(state.source == DiceSource.APP) { "Informe o resultado do dado físico." }
        require(state.dieSides > 0) { "Não há dado para rolar nesta etapa." }
        return applyResult(state, random(state.dieSides))
    }

    fun enterPhysicalResult(state: GameState, result: Int): GameState {
        require(state.source == DiceSource.PHYSICAL) { "Use a rolagem do aplicativo." }
        return applyResult(state, result)
    }

    private fun applyResult(state: GameState, result: Int): GameState {
        require(state.dieSides > 0 && result in 1..state.dieSides) { "Resultado fora dos limites do dado." }
        if (state.stage == Stage.ROLL_MINIMUM) {
            if (state.group == 0 && result == 4) {
                return state.copy(lastEvent = "D4: 4. Role o D4 novamente; nenhum atributo foi alterado.")
            }
            val position = if (state.group == 0) result - 1 else (result - 1) % 2
            val index = state.remaining[position]
            return state.copy(
                values = state.values.updated(index, 0),
                stage = Stage.ROLL_ATTRIBUTE,
                lastEvent = "D4: $result → ${CharacterRules.attributes[index].name} recebeu 0.",
            )
        }

        val index = state.remaining.first()
        val next = state.copy(
            values = state.values.updated(index, result),
            lastEvent = "D20: $result → ${CharacterRules.attributes[index].name}.",
        )
        if (next.remaining.isNotEmpty()) return next
        if (next.group == 1) return next.copy(stage = Stage.COMPLETE)
        return next.copy(
            group = 1,
            stage = if (state.mode == CreationMode.VILLAIN) Stage.ROLL_ATTRIBUTE else Stage.CHOOSE_MAXIMUM,
        )
    }

    private fun List<Int?>.updated(index: Int, value: Int): List<Int?> =
        mapIndexed { position, previous -> if (position == index) value else previous }
}
