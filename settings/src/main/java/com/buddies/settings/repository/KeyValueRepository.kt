package com.buddies.settings.repository

import android.content.Context
import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceDataStore
import com.buddies.common.util.safeLaunch
import com.buddies.settings.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class KeyValueRepository(
    private val applicationContext: Context
) : PreferenceDataStore() {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keyValue")
    private val dataStore = applicationContext.dataStore

    override fun putString(key: String?, value: String?) {
        key?.let {
            setStringValue(StringKey.fromKey(it, applicationContext), value ?: "")
        }
    }

    override fun getString(key: String?, defValue: String?): String? = runBlocking(scope.coroutineContext) {
        key?.let {
            getStringValue(StringKey.fromKey(it, applicationContext))
        } ?: defValue
    }

    fun setStringValue(key: StringKey, value: String) = scope.safeLaunch {
        dataStore.edit { preferences ->
            preferences[key.getPreferenceKey(applicationContext)] = value
        }
    }

    suspend fun getStringValue(key: StringKey): String? =
        dataStore.data.first()[key.getPreferenceKey(applicationContext)]

    enum class StringKey(@StringRes val preferenceKey: Int) {
        VERSION_NAME(R.string.version_preference_key),
        LOCATION_RADIUS(R.string.radius_preference_key);

        fun getPreferenceKey(context: Context) = stringPreferencesKey(context.getString(preferenceKey))

        companion object {
            fun fromKey(key: String, context: Context) = when (key) {
                context.getString(VERSION_NAME.preferenceKey) -> VERSION_NAME
                context.getString(LOCATION_RADIUS.preferenceKey) -> LOCATION_RADIUS
                else -> throw KeyNotFoundException()
            }
        }
    }

    class KeyNotFoundException : Exception("Preference key was not found.")
}