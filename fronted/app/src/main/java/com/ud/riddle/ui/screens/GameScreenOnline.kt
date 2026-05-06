package com.ud.riddle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ud.riddle.models.enums.GameStateEnum
import com.ud.riddle.viewmodels.GameViewModel
import kotlinx.coroutines.launch


@Composable
fun GameScreenOnline(viewModel: GameViewModel) {

    val game by viewModel.gameState.collectAsState()

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    var gameState by remember { mutableStateOf(GameStateEnum.CREATING_PLAYERS) }
    val players = remember { mutableStateListOf<Player>() }

    var secret by remember { mutableStateOf("cactus") }
    var impostorPosition: Int? = 0
    var positionClue by remember { mutableStateOf(0) }
    var currentPlayer: Player?
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }
    var isLoading by remember { mutableStateOf(false) }

    // gameConfig se llena cuando GameConfigScreen llama a onCreateGame
    var gameConfig by remember { mutableStateOf<Game?>(null) }

    when (gameState) {
        // ── 1. Agregar jugadores ──────────────────────────────────────────
        GameStateEnum.CREATING_PLAYERS -> {
            MatchScreen(viewModel)
        }

        // ── 2. Configurar partida ─────────────────────────────────────────
        GameStateEnum.GAME_CONFIG -> {
            GameConfigScreen(
                onCreateGame = { config ->
                    gameConfig = config
                    isLoading = true
                    scope.launch {
                        try {
                            val categoria = config.category.label
                            val wordFromApi = geminiService.generateSecretWord(categoria)

                            if (wordFromApi == "cactus") {
                                Toast.makeText(
                                    context,
                                    "La API devolvió el valor por defecto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            secret = wordFromApi

                            players.shuffle()
                            val randomPos = players.indices.random()
                            players.forEachIndexed { index, player ->
                                player.isImpostor = (index == randomPos)
                            }
                            impostorPosition = randomPos
                            gameState = GameStateEnum.SHOWING_CLUE

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            secret = "error_red"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )
        }

        // ── 3. Mostrar pistas ─────────────────────────────────────────────
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

        // ── 4. Turnos ─────────────────────────────────────────────────────
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

        // ── 5. Fin ────────────────────────────────────────────────────────
        GameStateEnum.END -> {
            Button(onClick = {
                val nameImpostor = impostorPosition?.let { players[it] }?.name
                Toast.makeText(context, "Impostor: $nameImpostor", Toast.LENGTH_LONG).show()
            }) { Text("Mostrar impostor") }
        }
    }
}