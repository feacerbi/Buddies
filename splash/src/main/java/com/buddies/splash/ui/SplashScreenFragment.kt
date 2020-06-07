package com.buddies.splash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.buddies.common.navigation.Navigator.NavDirection.SPLASH_TO_LOGIN
import com.buddies.common.navigation.Navigator.NavDirection.SPLASH_TO_PROFILE
import com.buddies.common.repository.UserRepository
import com.buddies.common.ui.NavigationFragment
import com.buddies.splash.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenFragment : NavigationFragment() {

    private val repository = UserRepository()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch {
            if (repository.isUserSignedIn(requireContext())) {
                navigate(SPLASH_TO_PROFILE)
            } else {
                delay(SPLASH_TIME)
                navigate(SPLASH_TO_LOGIN)
            }
        }

        return FragmentSplashScreenBinding.inflate(layoutInflater, container, false).root
    }

    companion object {
        private const val SPLASH_TIME = 2000L
    }
}
