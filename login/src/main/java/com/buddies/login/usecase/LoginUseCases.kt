package com.buddies.login.usecase

import android.content.Context
import android.content.Intent
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.LoginApi

class LoginUseCases(
    private val loginApi: LoginApi
) : BaseUseCases() {

    fun isUserLoggedIn(context: Context) = loginApi.isUserLoggedIn(context)

    suspend fun login(data: Intent?) = request {
        loginApi.login(data)
    }

    suspend fun checkNewUser() = request {
        loginApi.checkUserExists()
    }

}