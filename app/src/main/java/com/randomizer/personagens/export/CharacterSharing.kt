package com.randomizer.personagens.export

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.randomizer.personagens.game.GameState
import java.io.File
import java.util.UUID

internal object CharacterSharing {
    fun prepare(context: Context, game: GameState, name: String): Intent {
        val directory = File(context.cacheDir, "shared_characters")
        check(directory.isDirectory || directory.mkdirs())
        // Dá tempo aos aplicativos de destino para ler a imagem e limita o cache antigo.
        val expiration = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        directory.listFiles()?.filter { it.lastModified() < expiration }?.forEach { it.delete() }
        val file = File(directory, "randomizer-${UUID.randomUUID()}.png")
        try {
            file.outputStream().use { CharacterImageExporter.writePng(game, it, name) }
        } catch (failure: Exception) {
            file.delete()
            throw failure
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.characters", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("Personagem", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
