package com.buddies.server.api

import android.content.Context
import android.content.Intent
import com.buddies.common.model.UserInfo
import com.buddies.server.repository.UsersRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginApi(
    private val usersRepository: UsersRepository
) : BaseApi() {

    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn(context: Context) =
        GoogleSignIn.getLastSignedInAccount(context) != null && getCurrentUser() != null

    suspend fun login(
        data: Intent?
    ) = runWithResult {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        firebaseAuthWithGoogle(account?.idToken)
    }

    suspend fun checkUserExists(
    ) = runWithResult {
        val userSnapshot = usersRepository
            .getUser(usersRepository.getCurrentUserId())
            .handleTaskResult()

        if (userSnapshot.exists().not()) {
            runTransactions(
                usersRepository.setUser(UserInfo(
                    getCurrentUser()?.displayName ?: "",
                    getCurrentUser()?.email ?: "",
                    getCurrentUser()?.photoUrl.toString())
                )
            )
        }
    }

    private suspend fun firebaseAuthWithGoogle(
        idToken: String?
    ) = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
        .handleTaskResult()
}