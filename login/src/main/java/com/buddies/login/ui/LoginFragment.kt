package com.buddies.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.login.databinding.FragmentLoginBinding
import com.buddies.login.viewmodel.LoginViewModel
import com.buddies.login.viewmodel.LoginViewModel.Action
import com.buddies.login.viewmodel.LoginViewModel.Action.Login
import com.buddies.login.viewmodel.LoginViewModel.Action.LoginRequest
import com.buddies.login.viewstate.LoginViewEffect.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : NavigationFragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentLoginBinding.inflate(layoutInflater, container, false).apply {
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
        startActivityForResult(intent, SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN) {
            perform(Login(data))
        }
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    companion object {
        private const val SIGN_IN = 1
    }
}
