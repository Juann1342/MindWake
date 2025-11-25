import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Instancia única del DataStore
private val Context.dataStore by preferencesDataStore("riddle_progress")

class ProgressDataStore(private val context: Context) {

    companion object {
        private val KEY_RIDDLE_INDEX = intPreferencesKey("next_riddle_index")
        private val KEY_LATERAL_INDEX = intPreferencesKey("next_lateral_index")
        private val KEY_CYCLE_STEP = intPreferencesKey("cycle_step")

        // ✅ NUEVO: Clave para saber si ya vio el tutorial
        private val KEY_TUTORIAL_SHOWN = booleanPreferencesKey("tutorial_shown")
    }

    suspend fun saveIndexes(nextRiddle: Int, nextLateral: Int, cycleStep: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_RIDDLE_INDEX] = nextRiddle
            prefs[KEY_LATERAL_INDEX] = nextLateral
            prefs[KEY_CYCLE_STEP] = cycleStep
        }
    }

    // ✅ NUEVO: Guarda que el tutorial ya fue visto
    suspend fun setTutorialShown() {
        context.dataStore.edit { prefs ->
            prefs[KEY_TUTORIAL_SHOWN] = true
        }
    }

    suspend fun getNextRiddleIndex(): Int =
        context.dataStore.data.map { it[KEY_RIDDLE_INDEX] ?: 0 }.first()

    suspend fun getNextLateralIndex(): Int =
        context.dataStore.data.map { it[KEY_LATERAL_INDEX] ?: 0 }.first()

    suspend fun getCycleStep(): Int =
        context.dataStore.data.map { it[KEY_CYCLE_STEP] ?: 0 }.first()

    // ✅ NUEVO: Revisa si el tutorial ya fue mostrado (por defecto false)
    suspend fun isTutorialShown(): Boolean =
        context.dataStore.data.map { it[KEY_TUTORIAL_SHOWN] ?: false }.first()
}