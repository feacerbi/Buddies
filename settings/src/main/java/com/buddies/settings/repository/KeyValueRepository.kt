package com.buddies.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceDataStore
import com.buddies.common.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class KeyValueRepository(
    applicationContext: Context
) : PreferenceDataStore() {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keyValue")
    private val dataStore = applicationContext.dataStore

    override fun putString(key: String?, value: String?) {
        key?.let {
            setStringValue(StringKey.fromKey(it), value ?: "")
        }
    }

    override fun getString(key: String?, defValue: String?): String? = runBlocking(scope.coroutineContext) {
        key?.let {
            getStringValue(StringKey.fromKey(it))
        } ?: defValue
    }

    override fun putInt(key: String?, value: Int) {
        key?.let {
            setIntValue(IntKey.fromKey(it), value)
        }
    }

    override fun getInt(key: String?, defValue: Int): Int = runBlocking(scope.coroutineContext) {
        key?.let {
            getIntValue(IntKey.fromKey(it))
        } ?: defValue
    }

    fun setStringValue(key: StringKey, value: String) = scope.safeLaunch {
        dataStore.edit { preferences ->
            preferences[key.preferenceKey] = value
        }
    }

    suspend fun getStringValue(key: StringKey, default: String = ""): String =
        dataStore.data.first()[key.preferenceKey] ?: default

    fun setIntValue(key: IntKey, value: Int) = scope.safeLaunch {
        dataStore.edit { preferences ->
            preferences[key.preferenceKey] = value
        }
    }

    suspend fun getIntValue(key: IntKey, default: Int = 0): Int =
        dataStore.data.first()[key.preferenceKey] ?: default

    enum class StringKey(val preferenceKey: Preferences.Key<String>) {
        VERSION_NAME(stringPreferencesKey(VERSION_NAME_KEY)),
        LOCATION_RADIUS(stringPreferencesKey(LOCATION_RADIUS_KEY));

        companion object {
            fun fromKey(key: String) = when (key) {
                VERSION_NAME_KEY -> VERSION_NAME
                LOCATION_RADIUS_KEY -> LOCATION_RADIUS
                else -> throw KeyNotFoundException()
            }
        }
    }

    enum class IntKey(val preferenceKey: Preferences.Key<Int>) {
        /* no-op */
        NOOP(intPreferencesKey("noop"));

        companion object {
            fun fromKey(key: String): Nothing = when (key) {
                else -> throw KeyNotFoundException()
            }
        }
    }

    companion object {
        const val VERSION_NAME_KEY = "versionName"
        const val LOCATION_RADIUS_KEY = "locationRadius"
    }

    class KeyNotFoundException : Exception("Preference key was not found.")
}