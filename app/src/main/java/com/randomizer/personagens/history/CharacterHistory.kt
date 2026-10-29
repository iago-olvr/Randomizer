package com.randomizer.personagens.history

import android.util.AtomicFile
import com.randomizer.personagens.game.CharacterSummary
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import com.randomizer.personagens.game.GameState
import com.randomizer.personagens.game.Stage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

internal data class SavedCharacter(
    val id: String,
    val createdAt: Long,
    val name: String,
    val game: GameState,
) {
    init {
        require(id.isNotBlank())
        require(name.length <= 60)
        CharacterSummary.from(game)
    }
}

/** Arquivo privado com escrita atômica; uma falha de leitura nunca vira histórico vazio. */
internal class CharacterHistory(file: File) {
    private val storage = AtomicFile(file)

    @Synchronized
    fun load(): List<SavedCharacter> {
        val bytes = try {
            storage.openRead().use { it.readBytes() }
        } catch (missing: java.io.FileNotFoundException) {
            if (storage.baseFile.exists()) throw missing
            return emptyList()
        }
        val root = JSONObject(bytes.toString(Charsets.UTF_8))
        require(root.getInt("version") == 1) { "Formato de histórico desconhecido." }
        val entries = root.getJSONArray("characters")
        return List(entries.length()) { index ->
            val entry = entries.getJSONObject(index)
            val values = entry.getJSONArray("values")
            SavedCharacter(
                entry.getString("id"), entry.getLong("createdAt"), entry.getString("name"),
                GameState(
                    mode = CreationMode.valueOf(entry.getString("mode")),
                    source = DiceSource.valueOf(entry.getString("source")),
                    group = 1, stage = Stage.COMPLETE,
                    values = List(values.length()) { values.getInt(it) },
                    lastEvent = entry.getString("lastEvent"),
                ),
            )
        }.sortedByDescending { it.createdAt }
    }

    @Synchronized
    fun save(character: SavedCharacter): List<SavedCharacter> {
        val characters = (load().filterNot { it.id == character.id } + character)
            .sortedByDescending { it.createdAt }
        return write(characters)
    }

    @Synchronized
    fun delete(id: String): List<SavedCharacter> = write(load().filterNot { it.id == id })

    private fun write(characters: List<SavedCharacter>): List<SavedCharacter> {
        val entries = JSONArray()
        characters.forEach { saved ->
            entries.put(JSONObject().put("id", saved.id).put("createdAt", saved.createdAt)
                .put("name", saved.name).put("mode", saved.game.mode.name)
                .put("source", saved.game.source.name).put("values", JSONArray(saved.game.values))
                .put("lastEvent", saved.game.lastEvent))
        }
        val bytes = JSONObject().put("version", 1).put("characters", entries)
            .toString().toByteArray(Charsets.UTF_8)
        val output = storage.startWrite()
        try {
            output.write(bytes)
            storage.finishWrite(output)
        } catch (failure: Exception) {
            storage.failWrite(output)
            throw failure
        }
        return characters
    }
}
