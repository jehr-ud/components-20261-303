package com.ud.riddle.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ud.riddle.models.enums.GameCategory
import com.ud.riddle.models.enums.GameLanguages
import com.ud.riddle.models.enums.GameVisibility

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun CategoryCard(
    category: GameCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = category.icon, fontSize = 20.sp)
            Text(
                text = category.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LanguageChip(
    language: GameLanguages,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = "${language.flag} ${language.label}",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun VisibilityCard(
    visibility: GameVisibility,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.inverseSurface
        else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = visibility.icon, fontSize = 22.sp)
            Text(
                text = visibility.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.inverseOnSurface
                else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = visibility.description,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}