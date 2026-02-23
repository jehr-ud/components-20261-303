package com.ud.riddle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
    var impostorPosition: Int?
    var positionClue = 0

    when(gameState){

        GameStateEnum.CREATING_PLAYERS -> {

            Column(modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Input a name a player")
                TextField(
                    label = { Text("Label") },
                    value = name,
                    onValueChange = {
                        name = it
                    }
                )

                Button(onClick = {
                     players.add(Player(name=name))
                     name = ""
                }) { Text("Add") }

                Button(onClick = {
                    players.shuffle()
                    impostorPosition = players.indices.random()
                    players[impostorPosition].isImpostor = true

                    gameState = GameStateEnum.SHOWING_CLUE
                }) { Text("Start") }

                if (players.isNotEmpty()){
                    Text("Players:")

                    for (player in players){
                        Text(player.name)
                    }
                }

            }

        }
        GameStateEnum.SHOWING_CLUE -> {
            var currentPlayer = players[positionClue]

            Column(modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Take phone ${currentPlayer.name}")

                Button(onClick = {
                    val isImpostor =  currentPlayer.isImpostor

                    if (isImpostor){
                        Toast.makeText(context, "No tienes pista", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Pista: $secret", Toast.LENGTH_LONG).show()
                    }
                }) { Text("Show Clue") }


                Button(onClick = {
                    currentPlayer = players[positionClue]
                    positionClue++

                    if (positionClue == players.size){
                        gameState = GameStateEnum.IN_TURNS
                    }

                }) { Text("Next") }

            }

        }
        GameStateEnum.IN_TURNS -> {

        }
        GameStateEnum.END -> {

        }

    }
}