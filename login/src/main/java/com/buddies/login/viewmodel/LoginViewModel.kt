package com.buddies.login.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.LoginToHome
import com.buddies.common.navigation.Navigator.NavDirection.LoginToMissingFeed
import com.buddies.common.navigation.Navigator.NavDirection.SplashToHome
import com.buddies.common.navigation.Navigator.NavDirection.SplashToLogin
import com.buddies.common.navigation.Navigator.NavDirection.SplashToMissingFeed
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.configuration.Configuration
import com.buddies.configuration.Feature.HOME_SCREEN
import com.buddies.login.R
import com.buddies.login.usecase.LoginUseCases
import com.buddies.login.viewmodel.LoginViewModel.Action.Login
import com.buddies.login.viewmodel.LoginViewModel.Action.LoginRequest
import com.buddies.login.viewmodel.LoginViewModel.Action.StartSplashScreen
import com.buddies.login.viewstate.LoginViewEffect
import com.buddies.login.viewstate.LoginViewEffect.Navigate
import com.buddies.login.viewstate.LoginViewEffect.RequestLogin
import com.buddies.login.viewstate.LoginViewEffect.ShowError
import com.buddies.login.viewstate.LoginViewState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class LoginViewModel(
    private val useCases: LoginUseCases,
    private val configuration: Configuration
) : StateViewModel<LoginViewState, LoginViewEffect>(LoginViewState()), CoroutineScope {

    fun perform(action: Action) {
        when (action) {
            is StartSplashScreen -> handleSplashScreen(action.context)
            is LoginRequest -> requestLogin(action.activity)
            is Login -> login(action.permissionResultIntent)
        }
    }

    private fun handleSplashScreen(context: Context) = safeLaunch(::showError) {
        if (useCases.isUserLoggedIn(context)) {
            useCases.checkNewUser()
            val homeScreenEnabled = configuration.isFeatureEnabled(HOME_SCREEN)
            updateEffect(Navigate(if (homeScreenEnabled) SplashToHome else SplashToMissingFeed))
        } else {
            delay(SPLASH_TIME)
            updateEffect(Navigate(SplashToLogin))
        }
    }

    private fun requestLogin(activity: Activity) {
        updateEffect(RequestLogin(getLoginIntent(activity)))
    }

    private fun login(data: Intent?) = safeLaunch(::showError) {
        useCases.login(data)
        useCases.checkNewUser()

        val homeScreenEnabled = configuration.isFeatureEnabled(HOME_SCREEN)
        updateEffect(Navigate(if (homeScreenEnabled) LoginToHome else LoginToMissingFeed))
    }

    private fun getLoginIntent(activity: Activity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.oauth_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        return googleSignInClient.signInIntent
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        data class StartSplashScreen(val context: Context) : Action()
        data class LoginRequest(val activity: Activity) : Action()
        data class Login(val permissionResultIntent: Intent?) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    companion object {
        private const val SPLASH_TIME = 2000L
    }
}
