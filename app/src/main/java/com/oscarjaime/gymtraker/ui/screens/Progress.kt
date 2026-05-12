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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            EmptyState("Registra un ejercicio para consultar su evolución.")
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
    val lastSet  = exerciseSets.lastOrNull()
    val bestSet  = exerciseSets.maxByOrNull { it.weightKg }

    item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "Primera vez",
                value = firstSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "-",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Última vez",
                value = lastSet?.let { "${it.weightKg.cleanKg()} kg" } ?: "-",
                modifier = Modifier.weight(1f)
            )
        }
    }
    item {
        StatCard(
            label = "Marca máxima",
            value = bestSet?.let { "${it.weightKg.cleanKg()} kg × ${it.repetitions}" } ?: "-",
            modifier = Modifier.fillMaxWidth()
        )
    }
    item {
        SectionTitle("Histórico de $selectedExercise")
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
                    label = {
                        Text(
                            exercise,
                            fontWeight = if (exercise == selectedExercise) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ProgressChart(points: List<ProgressPoint>) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Peso máximo por día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Evolución en el tiempo",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            if (points.isEmpty()) {
                EmptyState("Sin datos para este ejercicio.")
            } else {
                val lineColor = MaterialTheme.colorScheme.primary
                val fillColorTop = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                val fillColorBottom = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
                val axisColor = MaterialTheme.colorScheme.outlineVariant
                val dotColor = MaterialTheme.colorScheme.surface

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val left   = 32.dp.toPx()
                    val right  = size.width - 12.dp.toPx()
                    val top    = 12.dp.toPx()
                    val bottom = size.height - 30.dp.toPx()
                    val minWeight = points.minOf { it.maxWeightKg }
                    val maxWeight = points.maxOf { it.maxWeightKg }
                    val range = (maxWeight - minWeight).takeIf { it > 0.0 } ?: 1.0

                    fun xFor(index: Int): Float {
                        val progress = index.toFloat() / (points.lastIndex.coerceAtLeast(1))
                        return left + progress * (right - left)
                    }

                    fun yFor(weight: Double): Float {
                        return bottom - ((weight - minWeight) / range).toFloat() * (bottom - top)
                    }

                    // Axis lines
                    drawLine(axisColor, Offset(left, bottom), Offset(right, bottom), strokeWidth = 1.5.dp.toPx())
                    drawLine(axisColor, Offset(left, top),    Offset(left, bottom),  strokeWidth = 1.5.dp.toPx())

                    if (points.size == 1) {
                        val x = (left + right) / 2
                        val y = yFor(points.first().maxWeightKg)
                        drawCircle(lineColor,  radius = 6.dp.toPx(), center = Offset(x, y))
                        drawCircle(dotColor,   radius = 3.dp.toPx(), center = Offset(x, y))
                    } else {
                        // Build fill path (area under the curve)
                        val fillPath = Path()
                        fillPath.moveTo(xFor(0), bottom)
                        points.forEachIndexed { index, point ->
                            fillPath.lineTo(xFor(index), yFor(point.maxWeightKg))
                        }
                        fillPath.lineTo(xFor(points.lastIndex), bottom)
                        fillPath.close()

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(fillColorTop, fillColorBottom),
                                startY = top,
                                endY = bottom
                            )
                        )

                        // Line path on top
                        val linePath = Path()
                        points.forEachIndexed { index, point ->
                            val x = xFor(index)
                            val y = yFor(point.maxWeightKg)
                            if (index == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                        }
                        drawPath(
                            path = linePath,
                            color = lineColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Dot points with white fill
                        points.forEachIndexed { index, point ->
                            val x = xFor(index)
                            val y = yFor(point.maxWeightKg)
                            drawCircle(lineColor, radius = 5.dp.toPx(),  center = Offset(x, y))
                            drawCircle(dotColor,  radius = 2.5.dp.toPx(), center = Offset(x, y))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        points.first().date.format(dateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        points.last().date.format(dateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}