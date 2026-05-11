package com.oscarjaime.gymtraker.ui.screens

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.oscarjaime.gymtraker.domain.TrainingSet

fun LazyListScope.HistoryScreen(trainingSets: List<TrainingSet>) {
    if (trainingSets.isEmpty()) {
        item { EmptyState("Tu historico aparecera aqui cuando registres entrenamientos.") }
        return
    }

    val groups = trainingSets
        .groupBy { it.dateMillis.toLocalDate() }
        .toSortedMap(compareByDescending { it })

    groups.forEach { (date, sets) ->
        item {
            SectionTitle(date.format(dateFormatter))
        }
        items(sets.sortedByDescending { it.dateMillis }, key = { it.id }) { set ->
            TrainingSetRow(set)
        }
    }
}
