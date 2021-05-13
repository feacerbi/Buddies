package com.buddies.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.settings.R
import com.buddies.settings.databinding.FragmentSettingsBinding

class SettingsFragment : NavigationFragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSettingsBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() = with (binding) {
        toolbar.title = getString(R.string.settings_screen_title)
        toolbar.setNavigationOnClickListener { navigateBack() }
    }
}