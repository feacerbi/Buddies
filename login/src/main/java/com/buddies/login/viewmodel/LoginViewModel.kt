package com.buddies.login.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.*
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.login.R
import com.buddies.login.usecase.LoginUseCases
import com.buddies.login.viewmodel.LoginViewModel.Action.*
import com.buddies.login.viewstate.LoginViewEffect
import com.buddies.login.viewstate.LoginViewEffect.*
import com.buddies.login.viewstate.LoginViewState
import com.buddies.login.viewstate.LoginViewStateReducer.ShowLoginOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

class LoginViewModel(
    private val useCases: LoginUseCases
) : StateViewModel<LoginViewState, LoginViewEffect>(LoginViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        updateState(ShowLoginOptions)
    }

    fun perform(action: Action) {
        when (action) {
            is StartSplashScreen -> handleSplashScreen(action.context)
            is LoginRequest -> requestLogin(action.activity)
            is Login -> login(action.permissionResultIntent)
        }
    }

    private fun handleSplashScreen(context: Context) = safeLaunch(::showError) {
        if (useCases.isUserLoggedIn(context)) {
            updateEffect(Navigate(SPLASH_TO_PROFILE))
        } else {
            delay(SPLASH_TIME)
            updateEffect(Navigate(SPLASH_TO_LOGIN))
        }
    }

    private fun requestLogin(activity: Activity) {
        updateEffect(RequestLogin(getLoginIntent(activity)))
    }

    private fun login(data: Intent?) = safeLaunch(::showError) {
        useCases.login(data)
        useCases.checkNewUser()
        updateEffect(Navigate(LOGIN_TO_PROFILE))
    }

    private fun getLoginIntent(activity: Activity): Intent {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
