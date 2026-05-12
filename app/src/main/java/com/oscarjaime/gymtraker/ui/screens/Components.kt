package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oscarjaime.gymtraker.domain.TrainingSet
import com.oscarjaime.gymtraker.ui.viewmodel.AppScreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val spanishLocale: Locale = Locale.forLanguageTag("es-ES")
internal val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", spanishLocale)
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", spanishLocale)

// Accent colors per muscle group for the left indicator strip
private fun muscleGroupColor(muscleGroup: String): Color = when (muscleGroup.lowercase()) {
    "pecho"    -> Color(0xFF4F7B18)
    "espalda"  -> Color(0xFF236487)
    "pierna"   -> Color(0xFF9D3D23)
    "hombro"   -> Color(0xFF7B5A18)
    "brazo"    -> Color(0xFF1A6B5A)
    "core"     -> Color(0xFF5A2D82)
    else       -> Color(0xFF6B6B6B)
}

@Composable
fun MainTopBar(
    title: String,
    username: String,
    selected: AppScreen,
    onOpen: (AppScreen) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Colored dot accent next to title
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            "@$username",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                    contentColor = MaterialTheme.colorScheme.error,
                    onClick = onLogout
                ) {
                    Text(
                        "Salir",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MenuButton("Inicio",    selected == AppScreen.DASHBOARD, { onOpen(AppScreen.DASHBOARD) }, Modifier.weight(1f))
                MenuButton("Entrenar", selected == AppScreen.WORKOUT,   { onOpen(AppScreen.WORKOUT)   }, Modifier.weight(1f))
                MenuButton("Historial",selected == AppScreen.HISTORY,   { onOpen(AppScreen.HISTORY)   }, Modifier.weight(1f))
                MenuButton("Progreso", selected == AppScreen.PROGRESS,  { onOpen(AppScreen.PROGRESS)  }, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val content   = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        color = container,
        contentColor = content,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TrainingSetRow(set: TrainingSet) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent strip colored by muscle group
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(72.dp)
                    .background(
                        muscleGroupColor(set.muscleGroup),
                        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                    )
            )
            Spacer(Modifier.width(12.dp))
            // Set number badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    set.setNumber.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    set.exerciseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${set.muscleGroup} · ${set.dateMillis.formatDateTime()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    "${set.weightKg.cleanKg()} kg",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "${set.repetitions} reps",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun Notice(text: String, isError: Boolean) {
    val bgColor   = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val accent    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(0.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(52.dp)
                .background(accent, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

internal fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun Long.formatDateTime(): String {
    val dateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    return "${dateTime.format(dateFormatter)} ${dateTime.format(timeFormatter)}"
}

internal fun Double.cleanKg(): String {
    return if (this % 1.0 == 0.0) this.toInt().toString() else String.format(Locale.US, "%.1f", this)
}