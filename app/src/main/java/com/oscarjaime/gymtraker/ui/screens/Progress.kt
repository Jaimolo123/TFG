package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oscarjaime.gymtraker.domain.ProgressPoint
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerUiState

fun LazyListScope.ProgressScreen(
    state: GymTrackerUiState,
    points: List<ProgressPoint>,
    onSelectExercise: (String) -> Unit
) {
    val selectedExercise = state.selectedExercise ?: state.exerciseNames.firstOrNull()

    item {
        if (state.exerciseNames.isEmpty()) {
            EmptyState("Registra un ejercicio para consultar su evolucion.")
        } else {
            ExerciseSelector(
                exercises = state.exerciseNames,
                selectedExercise = selectedExercise,
                onSelectExercise = onSelectExercise
            )
        }
    }

    if (selectedExercise == null) return

    item {
        ProgressChart(points = points)
    }

    val exerciseSets = state.userTrainingSets
        .filter { it.exerciseName.equals(selectedExercise, ignoreCase = true) }
        .sortedBy { it.dateMillis }
    val firstSet = exerciseSets.firstOrNull()
    val lastSet = exerciseSets.lastOrNull()
    val bestSet = exerciseSets.maxByOrNull { it.weightKg }

    item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "Primera vez",
                value = firstSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "-",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Ultima vez",
                value = lastSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "-",
                modifier = Modifier.weight(1f)
            )
        }
    }
    item {
        StatCard(
            label = "Marca maxima",
            value = bestSet?.let { "${it.weightKg.cleanKg()} kg x ${it.repetitions}" } ?: "-",
            modifier = Modifier.fillMaxWidth()
        )
    }
    item {
        SectionTitle("Historico de $selectedExercise")
    }
    items(exerciseSets, key = { it.id }) { set ->
        TrainingSetRow(set)
    }
}

@Composable
private fun ExerciseSelector(
    exercises: List<String>,
    selectedExercise: String?,
    onSelectExercise: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Ejercicio")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercises.forEach { exercise ->
                FilterChip(
                    selected = exercise == selectedExercise,
                    onClick = { onSelectExercise(exercise) },
                    label = { Text(exercise) }
                )
            }
        }
    }
}

@Composable
private fun ProgressChart(points: List<ProgressPoint>) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Peso maximo por dia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (points.isEmpty()) {
                EmptyState("Sin datos para este ejercicio.")
            } else {
                val lineColor = MaterialTheme.colorScheme.primary
                val axisColor = MaterialTheme.colorScheme.outlineVariant
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    val left = 30.dp.toPx()
                    val right = size.width - 12.dp.toPx()
                    val top = 12.dp.toPx()
                    val bottom = size.height - 30.dp.toPx()
                    val minWeight = points.minOf { it.maxWeightKg }
                    val maxWeight = points.maxOf { it.maxWeightKg }
                    val range = (maxWeight - minWeight).takeIf { it > 0.0 } ?: 1.0

                    drawLine(axisColor, Offset(left, bottom), Offset(right, bottom), strokeWidth = 2.dp.toPx())
                    drawLine(axisColor, Offset(left, top), Offset(left, bottom), strokeWidth = 2.dp.toPx())

                    if (points.size == 1) {
                        val x = (left + right) / 2
                        val y = bottom - ((points.first().maxWeightKg - minWeight) / range).toFloat() * (bottom - top)
                        drawCircle(lineColor, radius = 6.dp.toPx(), center = Offset(x, y))
                    } else {
                        val path = Path()
                        points.forEachIndexed { index, point ->
                            val progress = index.toFloat() / (points.lastIndex.coerceAtLeast(1))
                            val x = left + progress * (right - left)
                            val y = bottom - ((point.maxWeightKg - minWeight) / range).toFloat() * (bottom - top)
                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            drawCircle(lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
                        }
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(points.first().date.format(dateFormatter), style = MaterialTheme.typography.labelSmall)
                    Text(points.last().date.format(dateFormatter), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
