import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("progress")

class ProgressDataStore(private val context: Context) {

    companion object {
        val solvedCountKey = intPreferencesKey("solved_count")
        val lastIndexKey = intPreferencesKey("last_index")
        val cycleIndexKey = intPreferencesKey("cycle_index")
    }

    val solvedCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[solvedCountKey] ?: 0
    }

    suspend fun incrementSolved() {
        context.dataStore.edit { prefs ->
            val current = prefs[solvedCountKey] ?: 0
            prefs[solvedCountKey] = current + 1
        }
    }

    suspend fun saveIndexes(index: Int, cycleIndex: Int) {
        context.dataStore.edit { prefs ->
            prefs[lastIndexKey] = index
            prefs[cycleIndexKey] = cycleIndex
        }
    }

    val lastIndexFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[lastIndexKey] ?: 0
    }

    val cycleIndexFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[cycleIndexKey] ?: 0
    }

    suspend fun getLastIndex(): Int = lastIndexFlow.first()
    suspend fun getCycleIndex(): Int = cycleIndexFlow.first()
}
