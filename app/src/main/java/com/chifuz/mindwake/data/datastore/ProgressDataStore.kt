package com.chifuz.mindwake.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("progress")

class ProgressDataStore(private val context: Context) {

    companion object {
        val solvedCountKey = intPreferencesKey("solved_count")
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
}
