package com.namma.kathey.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "namma_prefs")

@Singleton
class PreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val USE_KANNADA = booleanPreferencesKey("use_kannada")
        val DARK_MODE   = booleanPreferencesKey("dark_mode")
    }
    val useKannada: Flow<Boolean> = context.dataStore.data.map { it[USE_KANNADA] ?: false }
    val darkMode:   Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE]   ?: false }

    suspend fun setUseKannada(v: Boolean) { context.dataStore.edit { it[USE_KANNADA] = v } }
    suspend fun setDarkMode(v: Boolean)   { context.dataStore.edit { it[DARK_MODE]   = v } }
}
