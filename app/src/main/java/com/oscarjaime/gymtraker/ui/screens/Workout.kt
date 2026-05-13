package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerUiState
import java.time.LocalDate

private val muscleGroups = listOf("Pecho", "Espalda", "Pierna", "Hombro", "Brazo", "Core", "Otros")

fun LazyListScope.WorkoutScreen(
    state: GymTrackerUiState,
    onAddTrainingSet: (String, String, String, String) -> Unit
) {
    item {
        WorkoutForm(onAddTrainingSet = onAddTrainingSet)
    }
    item {
        SectionTitle("Series de hoy")
    }
    val todaySets = state.userTrainingSets.filter { it.dateMillis.toLocalDate() == LocalDate.now() }
    if (todaySets.isEmpty()) {
        item { EmptyState("Guarda tu primera serie del día.") }
    } else {
        items(todaySets, key = { it.id }) { set ->
            TrainingSetRow(set)
        }
    }
}

@Composable
private fun WorkoutForm(onAddTrainingSet: (String, String, String, String) -> Unit) {
    var exerciseName       by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf(muscleGroups.last()) }
    var weight             by remember { mutableStateOf("") }
    var repetitions        by remember { mutableStateOf("") }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text(
                    "Anotar marca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Ejercicio · Grupo muscular · Peso · Reps",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it.filter { char -> !char.isDigit() }},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ejercicio o máquina") },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // Muscle group chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Grupo muscular",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    muscleGroups.forEach { muscle ->
                        FilterChip(
                            selected = selectedMuscleGroup == muscle,
                            onClick = { selectedMuscleGroup = muscle },
                            label = {
                                Text(
                                    muscle,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedMuscleGroup == muscle) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = repetitions,
                    onValueChange = { repetitions = it.filter { char -> char.isDigit() } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Repeticiones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            Button(
                onClick = {
                    onAddTrainingSet(exerciseName, selectedMuscleGroup, weight, repetitions)
                    if (exerciseName.isNotBlank() && weight.isNotBlank() && repetitions.isNotBlank()) {
                        weight = ""
                        repetitions = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Guardar serie",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}