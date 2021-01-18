package com.buddies.login.viewstate

import com.buddies.common.viewstate.ViewState

data class LoginViewState(
    val showSignInButton: Boolean = true
) : ViewState