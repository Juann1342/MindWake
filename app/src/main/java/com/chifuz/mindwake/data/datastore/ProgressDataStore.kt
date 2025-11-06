import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("riddle_progress")

class ProgressDataStore(private val context: Context) {

    companion object {
        private val KEY_RIDDLE_INDEX = intPreferencesKey("next_riddle_index")
        private val KEY_LATERAL_INDEX = intPreferencesKey("next_lateral_index")
        private val KEY_CYCLE_STEP = intPreferencesKey("cycle_step")
    }

    suspend fun saveIndexes(nextRiddle: Int, nextLateral: Int, cycleStep: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_RIDDLE_INDEX] = nextRiddle
            prefs[KEY_LATERAL_INDEX] = nextLateral
            prefs[KEY_CYCLE_STEP] = cycleStep
        }
    }

    suspend fun getNextRiddleIndex(): Int =
        context.dataStore.data.map { it[KEY_RIDDLE_INDEX] ?: 0 }.first()

    suspend fun getNextLateralIndex(): Int =
        context.dataStore.data.map { it[KEY_LATERAL_INDEX] ?: 0 }.first()

    suspend fun getCycleStep(): Int =
        context.dataStore.data.map { it[KEY_CYCLE_STEP] ?: 0 }.first()


}
