package com.randomizer.personagens.export

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.randomizer.personagens.game.CharacterGenerator
import com.randomizer.personagens.game.CreationMode
import com.randomizer.personagens.game.DiceSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterSharingTest {
    @Suppress("DEPRECATION")
    @Test fun compartilhaPngLegivelPorUriComPermissaoTemporaria() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val game = (1..7).fold(CharacterGenerator.start(CreationMode.VILLAIN, DiceSource.PHYSICAL)) {
            state, value -> CharacterGenerator.enterPhysicalResult(state, value)
        }
        val intent = CharacterSharing.prepare(context, game, "João, o Sábio")
        assertEquals(Intent.ACTION_SEND, intent.action)
        assertEquals("image/png", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        val uri = requireNotNull(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
        assertEquals("content", uri.scheme)
        assertEquals(uri, intent.clipData?.getItemAt(0)?.uri)
        val bitmap = context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }
        assertNotNull(bitmap)
        assertEquals(1080, bitmap.width)
        assertEquals(1280, bitmap.height)
        bitmap.recycle()
        context.contentResolver.delete(uri, null, null)
    }
}
