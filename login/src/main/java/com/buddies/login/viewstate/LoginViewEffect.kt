package com.buddies.login.viewstate

import android.content.Intent
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class LoginViewEffect : ViewEffect {
    data class RequestLogin(val intent: Intent) : LoginViewEffect()
    data class Navigate(val direction: NavDirection) : LoginViewEffect()
    data class ShowError(val error: Int) : LoginViewEffect()
}