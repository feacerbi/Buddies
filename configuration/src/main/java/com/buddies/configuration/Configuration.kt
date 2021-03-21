package com.buddies.configuration

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object Configuration {

    private val enabledFeaturesMap: MutableMap<Feature, Boolean> = mutableMapOf()
    private val availableFeaturesList: List<Feature> = Feature.values().toList()

    fun initialize(context: Context) {

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds =
                context.resources.getInteger(R.integer.minimum_fetch_interval_in_seconds).toLong()
        }

        with (Firebase.remoteConfig) {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)

            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    availableFeaturesList.forEach {
                        enabledFeaturesMap[it] = getBoolean(context.getString(it.key))
                    }
                }
            }
        }
    }

    fun isFeatureEnabled(feature: Feature) = enabledFeaturesMap[feature] ?: false
}