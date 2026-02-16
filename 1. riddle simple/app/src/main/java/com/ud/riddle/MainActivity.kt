package com.ud.riddle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ud.riddle.ui.theme.RiddleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RiddleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculateNames()
                }
            }
        }
    }
}

@Composable
fun CalculateNames(){
    val context = LocalContext.current
    Text("Input a name a player")
    TextField(
        label = { Text("Label") },
        value = "",
        onValueChange = {
            val toast = Toast.makeText(context, "Name $it", Toast.LENGTH_LONG)
             toast.show()
        }
    )
}