package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oscarjaime.gymtraker.ui.viewmodel.AppScreen
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerUiState

fun LazyListScope.DashboardScreen(
    state: GymTrackerUiState,
    onOpen: (AppScreen) -> Unit
) {
    val lastSet = state.userTrainingSets.maxByOrNull { it.dateMillis }
    val totalExercises = state.exerciseNames.size
    val totalSets = state.userTrainingSets.size
    val bestSet = state.userTrainingSets.maxByOrNull { it.weightKg }

    item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Hola, ${state.currentUser?.firstName.orEmpty()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(label = "Ejercicios", value = totalExercises.toString(), modifier = Modifier.weight(1f))
                StatCard(label = "Series", value = totalSets.toString(), modifier = Modifier.weight(1f))
            }
            StatCard(
                label = "Mejor peso",
                value = bestSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "Sin datos",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onOpen(AppScreen.WORKOUT) },
                modifier = Modifier.weight(1f)
            ) { Text("Entrenar") }
            OutlinedButton(
                onClick = { onOpen(AppScreen.PROGRESS) },
                modifier = Modifier.weight(1f),
                enabled = state.userTrainingSets.isNotEmpty()
            ) { Text("Progreso") }
        }
    }
    item {
        SectionTitle("Ultimas marcas")
    }
    if (lastSet == null) {
        item { EmptyState("Aun no hay series guardadas.") }
    } else {
        items(state.userTrainingSets.take(4), key = { it.id }) { set ->
            TrainingSetRow(set)
        }
    }
}
