package com.ud.riddle

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ud.riddle.models.enums.GameStateEnum
import com.ud.riddle.models.enums.Player
import com.ud.riddle.ui.theme.RiddleAppTheme

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
fun GameScreen(padding: PaddingValues){
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    var gameState by remember { mutableStateOf(GameStateEnum.CREATING_PLAYERS) }
    val players = remember { mutableStateListOf<Player>() }

    val secret = "cactus"
    var impostorPosition: Int? = 0
    var positionClue by remember { mutableStateOf(0) }
    var currentPlayer: Player?

    when(gameState){

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

                        Button(onClick = {
                            if (players.isNotEmpty()) {
                                players.shuffle()
                                impostorPosition = players.indices.random()
                                players[impostorPosition].isImpostor = true
                                gameState = GameStateEnum.SHOWING_CLUE
                            }
                        }) {
                            Text("Start")
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

            Column(modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Take phone ${currentPlayer?.name}")

                Button(onClick = {
                    val isImpostor =  currentPlayer?.isImpostor

                    if (isImpostor == true){
                        Toast.makeText(context, "No tienes pista", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Pista: $secret", Toast.LENGTH_LONG).show()
                    }
                }) { Text("Show Clue") }


                Button(onClick = {
                    currentPlayer = players[positionClue]
                    positionClue++

                    if (positionClue == players.size){
                        positionClue = 0
                        gameState = GameStateEnum.IN_TURNS
                    }

                }) { Text("Next") }

            }

        }
        GameStateEnum.IN_TURNS -> {
            currentPlayer = players[positionClue]

            Text("Take phone ${currentPlayer.name}")

            Button(onClick = {
                currentPlayer = players[positionClue]
                positionClue++

                if (positionClue == players.size){
                    gameState = GameStateEnum.END
                }

            }) { Text("Next") }

        }
        GameStateEnum.END -> {
            Button(onClick = {
                val nameImpostor = impostorPosition?.let { players[it] }?.name
                Toast.makeText(context, "Impostor $nameImpostor", Toast.LENGTH_LONG).show()

            }) { Text("Show impostor") }
        }

    }
}

@Preview
@Composable
fun GameScreenPreview(){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        GameScreen(innerPadding)
    }
}