package com.oscarjaime.gymtraker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oscarjaime.gymtraker.data.GymTrackerDataStore
import com.oscarjaime.gymtraker.domain.GymTrackerRepository
import com.oscarjaime.gymtraker.domain.ProgressPoint
import com.oscarjaime.gymtraker.domain.TrainingSet
import com.oscarjaime.gymtraker.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    REGISTER,
    DASHBOARD,
    WORKOUT,
    HISTORY,
    PROGRESS
}

data class GymTrackerUiState(
    val isLoading: Boolean = true,
    val screen: AppScreen = AppScreen.LOGIN,
    val currentUser: User? = null,
    val userTrainingSets: List<TrainingSet> = emptyList(),
    val selectedExercise: String? = null,
    val message: String? = null,
    val error: String? = null
) {
    val exerciseNames: List<String>
        get() = userTrainingSets
            .map { it.exerciseName }
            .distinctBy { it.lowercase() }
            .sorted()
}

class GymTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GymTrackerRepository(GymTrackerDataStore(application))

    private val _uiState = MutableStateFlow(GymTrackerUiState())
    val uiState: StateFlow<GymTrackerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.storage.collect { storage ->
                val activeUser = storage.users.firstOrNull { it.id == storage.activeUserId }
                _uiState.update { state ->
                    val userSets = storage.trainingSets
                        .filter { it.userId == activeUser?.id }
                        .sortedByDescending { it.dateMillis }

                    val nextScreen = when {
                        activeUser == null && state.screen != AppScreen.REGISTER -> AppScreen.LOGIN
                        activeUser != null && state.screen in listOf(AppScreen.LOGIN, AppScreen.REGISTER) -> AppScreen.DASHBOARD
                        else -> state.screen
                    }

                    val validSelectedExercise = state.selectedExercise
                        ?.takeIf { selected -> userSets.any { it.exerciseName.equals(selected, ignoreCase = true) } }

                    state.copy(
                        isLoading = false,
                        currentUser = activeUser,
                        userTrainingSets = userSets,
                        screen = nextScreen,
                        selectedExercise = validSelectedExercise
                    )
                }
            }
        }
    }

    fun open(screen: AppScreen) {
        _uiState.update { it.copy(screen = screen, error = null, message = null) }
    }

    fun showRegister() {
        _uiState.update { it.copy(screen = AppScreen.REGISTER, error = null, message = null) }
    }

    fun showLogin() {
        _uiState.update { it.copy(screen = AppScreen.LOGIN, error = null, message = null) }
    }

    fun register(firstName: String, lastName: String, email: String, username: String, password: String) {
        viewModelScope.launch {
            repository.register(firstName, lastName, email, username, password)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(screen = AppScreen.DASHBOARD, message = "Cuenta creada. Ya puedes registrar tus entrenamientos.", error = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message, message = null) }
                }
        }
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            repository.login(identifier, password)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(screen = AppScreen.DASHBOARD, message = "Sesion iniciada.", error = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message, message = null) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { it.copy(screen = AppScreen.LOGIN, selectedExercise = null, message = null, error = null) }
        }
    }

    fun addTrainingSet(exerciseName: String, muscleGroup: String, weight: String, repetitions: String) {
        val userId = _uiState.value.currentUser?.id ?: return
        viewModelScope.launch {
            repository.addTrainingSet(userId, exerciseName, muscleGroup, weight, repetitions)
                .onSuccess { trainingSet ->
                    _uiState.update {
                        it.copy(
                            selectedExercise = trainingSet.exerciseName,
                            message = "Serie guardada: ${trainingSet.exerciseName} ${trainingSet.weightKg.cleanKg()} kg x ${trainingSet.repetitions}.",
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message, message = null) }
                }
        }
    }

    fun selectExercise(exerciseName: String) {
        _uiState.update { it.copy(selectedExercise = exerciseName) }
    }

    fun progressForSelectedExercise(): List<ProgressPoint> {
        val state = _uiState.value
        val exerciseName = state.selectedExercise ?: state.exerciseNames.firstOrNull() ?: return emptyList()
        return repository.progressFor(exerciseName, state.userTrainingSets)
    }

    fun clearNotices() {
        _uiState.update { it.copy(message = null, error = null) }
    }

    private fun Double.cleanKg(): String {
        return if (this % 1.0 == 0.0) this.toInt().toString() else String.format("%.1f", this)
    }
}

