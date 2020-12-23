package com.buddies.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.common.util.registerForNonNullActivityResult
import com.buddies.login.databinding.FragmentLoginBinding
import com.buddies.login.viewmodel.LoginViewModel
import com.buddies.login.viewmodel.LoginViewModel.Action
import com.buddies.login.viewmodel.LoginViewModel.Action.Login
import com.buddies.login.viewmodel.LoginViewModel.Action.LoginRequest
import com.buddies.login.viewstate.LoginViewEffect.Navigate
import com.buddies.login.viewstate.LoginViewEffect.RequestLogin
import com.buddies.login.viewstate.LoginViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : NavigationFragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModel()

    private val login = registerForNonNullActivityResult(StartActivityForResult()) {
        perform(Login(it.data))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() {
        with (binding) {
            signInButton.setOnClickListener { perform(LoginRequest(requireActivity())) }
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            signInButton.isVisible = it.showSignInButton
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is RequestLogin -> requestLogin(it.intent)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun requestLogin(intent: Intent) {
        login.launch(intent)
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}
