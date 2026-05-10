package com.oscarjaime.gymtraker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.oscarjaime.gymtraker.domain.TrainingSet
import com.oscarjaime.gymtraker.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.gymTrackerDataStore by preferencesDataStore(name = "gym_tracker_store")

data class AppStorage(
    val users: List<User> = emptyList(),
    val trainingSets: List<TrainingSet> = emptyList(),
    val activeUserId: String? = null
)

class GymTrackerDataStore(private val context: Context) {
    private object Keys {
        val users = stringPreferencesKey("users_json")
        val trainingSets = stringPreferencesKey("training_sets_json")
        val activeUserId = stringPreferencesKey("active_user_id")
    }

    val storage: Flow<AppStorage> = context.gymTrackerDataStore.data.map { preferences ->
        AppStorage(
            users = decodeUsers(preferences[Keys.users].orEmpty()),
            trainingSets = decodeTrainingSets(preferences[Keys.trainingSets].orEmpty()),
            activeUserId = preferences[Keys.activeUserId]
        )
    }

    suspend fun saveUsers(users: List<User>) {
        context.gymTrackerDataStore.edit { preferences ->
            preferences[Keys.users] = encodeUsers(users)
        }
    }

    suspend fun saveTrainingSets(trainingSets: List<TrainingSet>) {
        context.gymTrackerDataStore.edit { preferences ->
            preferences[Keys.trainingSets] = encodeTrainingSets(trainingSets)
        }
    }

    suspend fun setActiveUser(userId: String?) {
        context.gymTrackerDataStore.edit { preferences ->
            if (userId == null) {
                preferences.remove(Keys.activeUserId)
            } else {
                preferences[Keys.activeUserId] = userId
            }
        }
    }

    private fun encodeUsers(users: List<User>): String {
        val array = JSONArray()
        users.forEach { user ->
            array.put(
                JSONObject()
                    .put("id", user.id)
                    .put("firstName", user.firstName)
                    .put("lastName", user.lastName)
                    .put("email", user.email)
                    .put("username", user.username)
                    .put("password", user.password)
            )
        }
        return array.toString()
    }

    private fun decodeUsers(rawJson: String): List<User> {
        if (rawJson.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(rawJson)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                User(
                    id = item.getString("id"),
                    firstName = item.optString("firstName"),
                    lastName = item.optString("lastName"),
                    email = item.optString("email"),
                    username = item.getString("username"),
                    password = item.getString("password")
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeTrainingSets(trainingSets: List<TrainingSet>): String {
        val array = JSONArray()
        trainingSets.forEach { set ->
            array.put(
                JSONObject()
                    .put("id", set.id)
                    .put("userId", set.userId)
                    .put("exerciseName", set.exerciseName)
                    .put("muscleGroup", set.muscleGroup)
                    .put("weightKg", set.weightKg)
                    .put("repetitions", set.repetitions)
                    .put("dateMillis", set.dateMillis)
                    .put("setNumber", set.setNumber)
            )
        }
        return array.toString()
    }

    private fun decodeTrainingSets(rawJson: String): List<TrainingSet> {
        if (rawJson.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(rawJson)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                TrainingSet(
                    id = item.getString("id"),
                    userId = item.getString("userId"),
                    exerciseName = item.getString("exerciseName"),
                    muscleGroup = item.optString("muscleGroup", "Otros"),
                    weightKg = item.getDouble("weightKg"),
                    repetitions = item.getInt("repetitions"),
                    dateMillis = item.getLong("dateMillis"),
                    setNumber = item.optInt("setNumber", 1)
                )
            }
        }.getOrDefault(emptyList())
    }
}

