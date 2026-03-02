package com.ud.riddle

import com.ud.riddle.R
import com.ud.riddle.models.enums.GameStateEnum
import com.ud.riddle.models.enums.Player
import com.ud.riddle.ui.theme.RiddleAppTheme
import com.ud.riddle.Service.GeminiService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RiddleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun GameScreen(padding: PaddingValues) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    var gameState by remember { mutableStateOf(GameStateEnum.CREATING_PLAYERS) }
    val players = remember { mutableStateListOf<Player>() }

    var secret by remember { mutableStateOf("cactus") }
    var word_clue by remember { mutableStateOf<String>("") }
    var impostorPosition by remember { mutableIntStateOf(0) }
    var positionClue by remember { mutableStateOf(0) }
    var currentPlayer: Player?
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }
    var isLoading by remember { mutableStateOf(false) }
    // NUEVO: categoría seleccionada
    var categoriaSeleccionada by remember { mutableStateOf(geminiService.categorias.first()) }
    var impostorName by remember { mutableStateOf<String?>("") }
    var showImpostor by remember { mutableStateOf(false) }

    when (gameState) {

        GameStateEnum.CREATING_PLAYERS -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Input a name a player")

                        Spacer(modifier = Modifier.height(12.dp))

                        TextField(
                            label = { Text("Player name") },
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
                            Text("Add")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // NUEVO: selector de categorías
                        Text("Categoría:")
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(geminiService.categorias) { cat ->
                                Button(
                                    onClick = { categoriaSeleccionada = cat },
                                    colors = if (categoriaSeleccionada == cat)
                                        ButtonDefaults.buttonColors()
                                    else
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                ) {
                                    Text(cat)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            enabled = !isLoading && players.isNotEmpty(),
                            onClick = {

                                if (players.isNotEmpty() && players.size >= 3) {


                                isLoading = true
                                scope.launch {
                                    try {
                                        // CAMBIADO: pasa la categoría seleccionada
                                        val wordFromApi =
                                            geminiService.generateSecretWord(categoriaSeleccionada)

                                        if (wordFromApi == "cactus") {
                                            Toast.makeText(
                                                context,
                                                "La API devolvió el valor por defecto",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                        secret = wordFromApi
                                        players.shuffle()
                                        impostorPosition = players.indices.random()
                                        players[impostorPosition].isImpostor = true
                                        gameState = GameStateEnum.SHOWING_CLUE

                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        secret = "error_red"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                                }else {
                                    Toast.makeText(
                                        context,
                                        "there are few players",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        ) {
                            Text(if (isLoading) "Consultando IA..." else "Start")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (players.isNotEmpty()) {
                            Text("Players:")

                            players.forEach { player ->
                                Text(player.name)
                            }
                        }
                    }
                }
            }
        }

        GameStateEnum.SHOWING_CLUE -> {
            currentPlayer = players[positionClue]

            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Take phone ${currentPlayer?.name}")

                Button(onClick = {
                    val isImpostor = currentPlayer?.isImpostor

                    word_clue = if (isImpostor == true) {
                        "TU ERES EL IMMPORTOR, tu pista es: no pista"
                    } else {
                        "la palabra es: $secret"
                    }
                }) { Text("Show Clue") }


                Button(onClick = {
                    currentPlayer = players[positionClue]
                    positionClue++
                    word_clue = ""
                    if (positionClue == players.size) {
                        positionClue = 0
                        gameState = GameStateEnum.IN_TURNS
                    }

                }) { Text("Next") }
                word_clue?.let { word_clue ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = word_clue
                    )
                }
            }

        }

        GameStateEnum.IN_TURNS -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentPlayer = players[positionClue]

                Text("Take phone ${currentPlayer.name}")

                Button(onClick = {
                    currentPlayer = players[positionClue]
                    positionClue++

                    if (positionClue == players.size) {
                        gameState = GameStateEnum.END
                    }

                }) { Text("Next") }
            }
        }

        GameStateEnum.END -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {

                    impostorName = impostorPosition?.let { players[it] }?.name
                    showImpostor = true

                    //Toast.makeText(context, "Impostor $nameImpostor", Toast.LENGTH_LONG).show()

                }) { Text("Show impostor") }
                Button(onClick = {
                    positionClue = 0
                    gameState = GameStateEnum.IN_TURNS
                }) { Text("New round to say the word") }
                if (showImpostor && impostorName != null) {
                    impostorName?.let { name ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "El impostor es: $name"
                        )
                    }
                    LaunchedEffect(showImpostor) {
                        delay(5000L)
                        showImpostor = false
                        gameState = GameStateEnum.NEWGAME
                    }
                }
            }
        }
        GameStateEnum.NEWGAME -> {
            Column(modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(8.dp))

                //selector de categorías
                Text("Seleccione una categoria (opcional si van a jugar los mismos):")
                Text("Categoría:")
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(geminiService.categorias) { cat ->
                        Button(
                            onClick = { categoriaSeleccionada = cat },
                            colors = if (categoriaSeleccionada == cat)
                                ButtonDefaults.buttonColors()
                            else
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        ) {
                            Text(cat)
                        }
                    }
                }
                Button(onClick = {
                    impostorName=null
                    positionClue = 0
                    for(i in 0..(players.size-1)) {
                        players[i].isImpostor = false
                    }
                    isLoading = true
                    scope.launch {
                        try {
                            // CAMBIADO: pasa la categoría seleccionada
                            val wordFromApi =
                                geminiService.generateSecretWord(categoriaSeleccionada)

                            if (wordFromApi == "cactus") {
                                Toast.makeText(
                                    context,
                                    "La API devolvió el valor por defecto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            secret = wordFromApi
                            players.shuffle()
                            impostorPosition = players.indices.random()
                            players[impostorPosition].isImpostor = true
                            gameState = GameStateEnum.SHOWING_CLUE

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            secret = "error_red"
                        } finally {
                            isLoading = false
                        }
                    }
                }) { Text("New game whit the same players") }
                Button(onClick = {
                    players.clear()
                    positionClue=0
                    gameState = GameStateEnum.CREATING_PLAYERS
                }) { Text("New game whit new players") }
            }
        }
    }
}