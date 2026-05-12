package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oscarjaime.gymtraker.ui.viewmodel.AppScreen
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerUiState

fun LazyListScope.DashboardScreen(
    state: GymTrackerUiState,
    onOpen: (AppScreen) -> Unit
) {
    val lastSet       = state.userTrainingSets.maxByOrNull { it.dateMillis }
    val totalExercises = state.exerciseNames.size
    val totalSets     = state.userTrainingSets.size
    val bestSet       = state.userTrainingSets.maxByOrNull { it.weightKg }

    item {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Greeting header
            Column {
                Text(
                    "Hola,",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    state.currentUser?.firstName.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }

            // Stats row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    label = "Ejercicios",
                    value = totalExercises.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Series",
                    value = totalSets.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Best weight highlight card
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MEJOR PESO".uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            bestSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "Sin datos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    bestSet?.let {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                it.exerciseName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "${it.repetitions} reps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onOpen(AppScreen.WORKOUT) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Entrenar", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { onOpen(AppScreen.PROGRESS) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = state.userTrainingSets.isNotEmpty()
            ) {
                Text("Progreso", fontWeight = FontWeight.Bold)
            }
        }
    }

    item {
        SectionTitle("Últimas marcas")
    }

    if (lastSet == null) {
        item { EmptyState("Aún no hay series guardadas.") }
    } else {
        items(state.userTrainingSets.take(4), key = { it.id }) { set ->
            TrainingSetRow(set)
        }
    }
}