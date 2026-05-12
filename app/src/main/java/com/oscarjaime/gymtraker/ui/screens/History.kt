package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oscarjaime.gymtraker.domain.TrainingSet

fun LazyListScope.HistoryScreen(trainingSets: List<TrainingSet>) {
    if (trainingSets.isEmpty()) {
        item { EmptyState("Tu histórico aparecerá aquí cuando registres entrenamientos.") }
        return
    }

    val groups = trainingSets
        .groupBy { it.dateMillis.toLocalDate() }
        .toSortedMap(compareByDescending { it })

    groups.forEach { (date, sets) ->
        item {
            HistoryDateHeader(label = date.format(dateFormatter), count = sets.size)
        }
        items(sets.sortedByDescending { it.dateMillis }, key = { it.id }) { set ->
            TrainingSetRow(set)
        }
    }
}

@Composable
private fun HistoryDateHeader(label: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.2.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                "$count series",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}