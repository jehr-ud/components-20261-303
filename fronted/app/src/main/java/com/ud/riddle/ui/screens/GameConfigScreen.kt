package com.ud.riddle.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ud.riddle.models.Game
import com.ud.riddle.models.enums.GameCategory
import com.ud.riddle.models.enums.GameLanguages
import com.ud.riddle.models.enums.GameVisibility

@Composable
fun GameConfigScreen(
    onCreateGame: (Game) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(GameCategory.MOVIES) }
    var selectedLanguage by remember { mutableStateOf(GameLanguages.SPANISH) }
    var selectedVisibility by remember { mutableStateOf(GameVisibility.PRIVATE) }
    val roomCode = remember { generateRoomCode() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Nueva partida",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(28.dp))

        // --- Categoría ---
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

        // --- Tipo de partida ---
        SectionLabel("Tipo de partida")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameVisibility.entries.forEach { visibility ->
                VisibilityCard(
                    visibility = visibility,
                    isSelected = selectedVisibility == visibility,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedVisibility = visibility }
                )
            }
        }

        // Código de sala (solo visible en modo privado)
        if (selectedVisibility == GameVisibility.PRIVATE) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Código de sala",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = roomCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                onCreateGame(
                    Game(
                        category = selectedCategory,
                        language = selectedLanguage,
                        visibility = selectedVisibility
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear partida", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}



private fun generateRoomCode(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return "${(0..1).map { chars.random() }.joinToString("")}-${(0..3).map { chars.filter { c -> c.isDigit() }.random() }.joinToString("")}"
}