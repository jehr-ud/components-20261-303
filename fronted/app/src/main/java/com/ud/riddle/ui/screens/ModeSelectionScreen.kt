package com.ud.riddle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ModeSelectionScreen(onModeSelected: (Boolean) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = { onModeSelected(false) }) {
            Text("Juego local (todos juegan en un solo dispositivo)")
        }

        Button(onClick = { onModeSelected(true) }) {
            Text("Juego online (cada uno con su di)")
        }
    }
}