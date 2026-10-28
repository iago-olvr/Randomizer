package com.randomizer.personagens.game

internal val CreationMode.displayName: String
    get() = if (this == CreationMode.VILLAIN) "Vilão" else "Personagem"

internal data class AttributeValue(val name: String, val value: Int)

/** Retrato completo dos valores, independente da tela e do tamanho do aparelho. */
internal data class CharacterSummary(val mode: String, val groups: List<List<AttributeValue>>) {
    companion object {
        fun from(state: GameState): CharacterSummary {
            require(state.stage == Stage.COMPLETE && state.values.size == 7 &&
                state.values.all { it != null && it in 0..20 }) {
                "Preencha todos os atributos antes de exportar."
            }
            return CharacterSummary(state.mode.displayName, CharacterRules.groups.map { group ->
                group.map { index ->
                    AttributeValue(CharacterRules.attributes[index].name, requireNotNull(state.values[index]))
                }
            })
        }
    }
}
