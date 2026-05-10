package com.oscarjaime.gymtraker.domain

import com.oscarjaime.gymtraker.data.AppStorage
import com.oscarjaime.gymtraker.data.GymTrackerDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String
)

data class TrainingSet(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val weightKg: Double,
    val repetitions: Int,
    val dateMillis: Long = System.currentTimeMillis(),
    val setNumber: Int
) {
    val volume: Double
        get() = weightKg * repetitions
}

data class ProgressPoint(
    val date: LocalDate,
    val maxWeightKg: Double,
    val maxRepetitions: Int,
    val totalVolume: Double
)

class GymTrackerRepository(private val dataStore: GymTrackerDataStore) {
    val storage: Flow<AppStorage> = dataStore.storage

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String
    ): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (firstName.isBlank() || lastName.isBlank() || cleanEmail.isBlank() || cleanUsername.isBlank() || cleanPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("Rellena todos los campos para crear la cuenta."))
        }

        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Introduce un correo valido."))
        }

        val storage = storage.first()
        val alreadyExists = storage.users.any { user ->
            user.username.equals(cleanUsername, ignoreCase = true) ||
                user.email.equals(cleanEmail, ignoreCase = true)
        }
        if (alreadyExists) {
            return Result.failure(IllegalArgumentException("Ya existe un usuario con ese correo o nombre de usuario."))
        }

        val user = User(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = cleanEmail,
            username = cleanUsername,
            password = cleanPassword
        )
        dataStore.saveUsers(storage.users + user)
        dataStore.setActiveUser(user.id)
        return Result.success(user)
    }

    suspend fun login(identifier: String, password: String): Result<User> {
        val storage = storage.first()
        val cleanIdentifier = identifier.trim()
        val cleanPassword = password.trim()
        val user = storage.users.firstOrNull {
            (it.username.equals(cleanIdentifier, ignoreCase = true) ||
                it.email.equals(cleanIdentifier, ignoreCase = true)) &&
                it.password == cleanPassword
        } ?: return Result.failure(IllegalArgumentException("Usuario o contrasena incorrectos."))

        dataStore.setActiveUser(user.id)
        return Result.success(user)
    }

    suspend fun logout() {
        dataStore.setActiveUser(null)
    }

    suspend fun addTrainingSet(
        userId: String,
        exerciseName: String,
        muscleGroup: String,
        weight: String,
        repetitions: String
    ): Result<TrainingSet> {
        val cleanExercise = exerciseName.trim()
        val parsedWeight = weight.replace(',', '.').toDoubleOrNull()
        val parsedRepetitions = repetitions.toIntOrNull()

        if (cleanExercise.isBlank()) {
            return Result.failure(IllegalArgumentException("Escribe el nombre del ejercicio."))
        }
        if (parsedWeight == null || parsedWeight <= 0.0) {
            return Result.failure(IllegalArgumentException("El peso debe ser mayor que 0."))
        }
        if (parsedRepetitions == null || parsedRepetitions <= 0) {
            return Result.failure(IllegalArgumentException("Las repeticiones deben ser mayores que 0."))
        }

        val storage = storage.first()
        val setNumber = storage.trainingSets
            .filter {
                it.userId == userId &&
                    it.exerciseName.equals(cleanExercise, ignoreCase = true) &&
                    it.dateMillis.toLocalDate() == LocalDate.now()
            }
            .size + 1

        val trainingSet = TrainingSet(
            userId = userId,
            exerciseName = cleanExercise.replaceFirstChar { it.uppercase() },
            muscleGroup = muscleGroup.ifBlank { "Otros" },
            weightKg = parsedWeight,
            repetitions = parsedRepetitions,
            setNumber = setNumber
        )
        dataStore.saveTrainingSets(storage.trainingSets + trainingSet)
        return Result.success(trainingSet)
    }

    fun progressFor(exerciseName: String, trainingSets: List<TrainingSet>): List<ProgressPoint> {
        return trainingSets
            .filter { it.exerciseName.equals(exerciseName, ignoreCase = true) }
            .groupBy { it.dateMillis.toLocalDate() }
            .map { (date, sets) ->
                ProgressPoint(
                    date = date,
                    maxWeightKg = sets.maxOf { it.weightKg },
                    maxRepetitions = sets.maxOf { it.repetitions },
                    totalVolume = sets.sumOf { it.volume }
                )
            }
            .sortedBy { it.date }
    }

    private fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}

