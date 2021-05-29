package com.buddies.settings.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.buddies.settings.R
import com.buddies.settings.repository.KeyValueRepository
import com.buddies.settings.repository.KeyValueRepository.StringKey
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PreferencesFragment : PreferenceFragmentCompat() {

    private val keyValueRepository: KeyValueRepository by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = keyValueRepository
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpVersionPreference()
    }

    private fun setUpVersionPreference() = lifecycleScope.launch {
        val versionName = keyValueRepository.getStringValue(StringKey.VERSION_NAME)
        val versionPreference = findPreference<Preference>(getString(R.string.version_preference_key))
        versionPreference?.title = versionName
    }
}