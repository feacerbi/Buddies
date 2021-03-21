package com.buddies.configuration.initializer

import android.content.Context
import androidx.startup.Initializer
import com.buddies.configuration.Configuration

class ConfigurationInitializer : Initializer<Configuration> {

    override fun create(context: Context): Configuration = Configuration.apply {
        initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}