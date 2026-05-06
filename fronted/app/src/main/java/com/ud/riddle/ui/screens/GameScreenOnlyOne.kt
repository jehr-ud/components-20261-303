package com.ud.riddle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ud.riddle.service.GeminiService
import com.ud.riddle.models.Game
import com.ud.riddle.models.Player
import com.ud.riddle.models.enums.GameCategory
import com.ud.riddle.models.enums.GameLanguages
import com.ud.riddle.models.enums.GameStateEnum
import com.ud.riddle.models.enums.GameVisibility
import com.ud.riddle.viewmodels.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun GameScreenOnlyOne(viewModel: GameViewModel) {

    val game by viewModel.gameState.collectAsState()

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    var gameState by remember { mutableStateOf(GameStateEnum.CREATING_PLAYERS) }
    val players = remember { mutableStateListOf<Player>() }

    var secret by remember { mutableStateOf("cactus") }
    val impostorPosition: Int = 0
    var positionClue by remember { mutableStateOf(0) }
    var currentPlayer: Player?

    when (gameState) {
        GameStateEnum.CREATING_PLAYERS -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ingresa el nombre del jugador")

                        Spacer(modifier = Modifier.height(12.dp))

                        TextField(
                            label = { Text("Nombre") },
                            value = name,
                            onValueChange = { name = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = {
                            if (name.isNotBlank()) {
                                players.add(Player(name = name))
                                name = ""
                            }
                        }) {
                            Text("Agregar")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (players.isNotEmpty()) {
                            Text("Jugadores:")
                            players.forEach { player -> Text(player.name) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        Button(
                            enabled = players.isNotEmpty(),
                            onClick = { gameState = GameStateEnum.GAME_CONFIG }
                        ) {
                            Text("EMPEZAR →")
                        }
                    }
                }
            }
        }

        GameStateEnum.GAME_CONFIG -> {
            var selectedCategory by remember { mutableStateOf(GameCategory.MOVIES) }
            var selectedLanguage by remember { mutableStateOf(GameLanguages.SPANISH) }

            SectionLabel("Categoría")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameCategory.entries.forEach { category ->
                    CategoryCard(
                        category = category,
                        isSelected = selectedCategory == category,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            // --- Idioma ---
            SectionLabel("Idioma")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameLanguages.entries.forEach { language ->
                    LanguageChip(
                        language = language,
                        isSelected = selectedLanguage == language,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedLanguage = language }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))
        }

        GameStateEnum.SHOWING_CLUE -> {
            currentPlayer = players[positionClue]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pasa el teléfono a ${currentPlayer?.name}")

                Button(onClick = {
                    if (currentPlayer?.isImpostor == true) {
                        Toast.makeText(context, "No tienes pista", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Pista: $secret", Toast.LENGTH_LONG).show()
                    }
                }) { Text("Ver pista") }

                Button(onClick = {
                    currentPlayer = players[positionClue]
                    positionClue++
                    if (positionClue == players.size) {
                        positionClue = 0
                        gameState = GameStateEnum.IN_TURNS
                    }
                }) { Text("Siguiente") }
            }
        }

        GameStateEnum.IN_TURNS -> {
            currentPlayer = players[positionClue]

            Text("Pasa el teléfono a ${currentPlayer.name}")

            Button(onClick = {
                currentPlayer = players[positionClue]
                positionClue++
                if (positionClue == players.size) {
                    gameState = GameStateEnum.END
                }
            }) { Text("Siguiente") }
        }

        GameStateEnum.END -> {
            Button(onClick = {
                val nameImpostor = impostorPosition?.let { players[it] }?.name
                Toast.makeText(context, "Impostor: $nameImpostor", Toast.LENGTH_LONG).show()
            }) { Text("Mostrar impostor") }
        }
    }
}