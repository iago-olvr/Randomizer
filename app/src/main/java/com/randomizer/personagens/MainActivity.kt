package com.randomizer.personagens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.randomizer.personagens.design.MedievalColors
import com.randomizer.personagens.ui.RandomizerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, MedievalColors.Ink),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, MedievalColors.Ink),
        )
        setContent { RandomizerApp() }
    }
}
