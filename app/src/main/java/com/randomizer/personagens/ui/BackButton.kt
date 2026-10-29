package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BackButton(onClick: () -> Unit, enabled: Boolean, description: String = "Voltar") {
    OutlinedButton(onClick = onClick, enabled = enabled, shape = CircleShape,
        modifier = Modifier.size(52.dp).semantics { contentDescription = description },
        contentPadding = PaddingValues(0.dp)) {
        Text("←", fontSize = 28.sp, modifier = Modifier.clearAndSetSemantics {})
    }
}
