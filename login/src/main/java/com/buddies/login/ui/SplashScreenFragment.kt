package com.buddies.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.login.databinding.FragmentSplashScreenBinding
import com.buddies.login.viewmodel.LoginViewModel
import com.buddies.login.viewmodel.LoginViewModel.Action.StartSplashScreen
import com.buddies.login.viewstate.LoginViewEffect.Navigate
import com.buddies.login.viewstate.LoginViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashScreenFragment : NavigationFragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        perform(StartSplashScreen(requireContext()))
    }

    private fun bindViews() {
        observe(viewModel.getEffectStream()) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
                else -> { /* Ignore */ }
            }
        }
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: LoginViewModel.Action) {
        viewModel.perform(action)
    }
}
