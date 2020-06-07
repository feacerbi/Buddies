package com.buddies.login.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.buddies.common.navigation.Navigator.NavDirection.LOGIN_TO_PROFILE
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.login.R
import com.buddies.login.viewmodel.LoginViewModel.Action.SignIn
import com.buddies.login.viewmodel.LoginViewModel.Action.SignInResult
import com.buddies.login.viewstate.LoginViewEffect
import com.buddies.login.viewstate.LoginViewEffect.*
import com.buddies.login.viewstate.LoginViewState
import com.buddies.login.viewstate.LoginViewStateReducer.ShowLoginOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class LoginViewModel
    : StateViewModel<LoginViewState, LoginViewEffect>(LoginViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private val auth = FirebaseAuth.getInstance()

    init {
        updateState(ShowLoginOptions)
    }

    fun perform(action: Action) {
        when (action) {
            is SignIn -> signIn(action.activity)
            is SignInResult -> signInResult(action.activity, action.intent)
        }
    }

    private fun signIn(activity: Activity) {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.oauth_id))
                .requestEmail()
                .build()

        val googleSignInClient =
            GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent

        updateEffect(RequestLogin(signInIntent))
    }

    private fun signInResult(activity: Activity, data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account?.idToken, activity)
        } catch (e: ApiException) {
            updateEffect(ShowError(e.message))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?, activity: Activity) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    updateEffect(Navigate(LOGIN_TO_PROFILE))
                } else {
                    updateEffect(ShowError(task.exception?.message))
                }
            }
    }

    sealed class Action {
        data class SignIn(val activity: Activity) : Action()
        data class SignInResult(val activity: Activity, val intent: Intent?) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
