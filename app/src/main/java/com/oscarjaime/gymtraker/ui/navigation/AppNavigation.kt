package com.oscarjaime.gymtraker.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oscarjaime.gymtraker.domain.ProgressPoint
import com.oscarjaime.gymtraker.ui.screens.DashboardScreen
import com.oscarjaime.gymtraker.ui.screens.HistoryScreen
import com.oscarjaime.gymtraker.ui.screens.LoadingScreen
import com.oscarjaime.gymtraker.ui.screens.LoginScreen
import com.oscarjaime.gymtraker.ui.screens.MainTopBar
import com.oscarjaime.gymtraker.ui.screens.Notice
import com.oscarjaime.gymtraker.ui.screens.ProgressScreen
import com.oscarjaime.gymtraker.ui.screens.RegisterScreen
import com.oscarjaime.gymtraker.ui.screens.WorkoutScreen
import com.oscarjaime.gymtraker.ui.viewmodel.AppScreen
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerUiState
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerViewModel

@Composable
fun AppNavigation(viewModel: GymTrackerViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.message, state.error) {
        if (state.message != null || state.error != null) {
            kotlinx.coroutines.delay(3200)
            viewModel.clearNotices()
        }
    }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.screen) {
            AppScreen.LOGIN -> LoginScreen(
                error = state.error,
                onLogin = viewModel::login,
                onRegisterClick = viewModel::showRegister
            )

            AppScreen.REGISTER -> RegisterScreen(
                error = state.error,
                onRegister = viewModel::register,
                onLoginClick = viewModel::showLogin
            )

            else -> MainScaffold(
                state = state,
                progressPoints = viewModel.progressForSelectedExercise(),
                onOpen = viewModel::open,
                onLogout = viewModel::logout,
                onAddTrainingSet = viewModel::addTrainingSet,
                onSelectExercise = viewModel::selectExercise
            )
        }
    }
}

@Composable
private fun MainScaffold(
    state: GymTrackerUiState,
    progressPoints: List<ProgressPoint>,
    onOpen: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    onAddTrainingSet: (String, String, String, String) -> Unit,
    onSelectExercise: (String) -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBar(
                title = titleFor(state.screen),
                username = state.currentUser?.username.orEmpty(),
                selected = state.screen,
                onOpen = onOpen,
                onLogout = onLogout
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.message?.let { message ->
                item { Notice(text = message, isError = false) }
            }
            state.error?.let { error ->
                item { Notice(text = error, isError = true) }
            }

            when (state.screen) {
                AppScreen.WORKOUT -> WorkoutScreen(
                    state = state,
                    onAddTrainingSet = onAddTrainingSet
                )

                AppScreen.HISTORY -> HistoryScreen(trainingSets = state.userTrainingSets)
                AppScreen.PROGRESS -> ProgressScreen(
                    state = state,
                    points = progressPoints,
                    onSelectExercise = onSelectExercise
                )

                else -> DashboardScreen(state = state, onOpen = onOpen)
            }
        }
    }
}

private fun titleFor(screen: AppScreen): String {
    return when (screen) {
        AppScreen.WORKOUT -> "Entrenamiento"
        AppScreen.HISTORY -> "Historial"
        AppScreen.PROGRESS -> "Progreso"
        else -> "Inicio"
    }
}
