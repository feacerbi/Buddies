package com.buddies.common.repository

import android.content.Context
import android.net.Uri
import com.buddies.common.model.User
import com.buddies.common.repository.UserRepository.RequestResult.Fail
import com.buddies.common.repository.UserRepository.RequestResult.Success
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    fun isUserSignedIn(context: Context) =
        GoogleSignIn.getLastSignedInAccount(context) != null && currentUser != null

    fun getCurrentUser() = if (currentUser == null) null else
        User(
            currentUser.uid,
            currentUser.displayName,
            currentUser.email,
            currentUser.photoUrl
        )

    suspend fun updateName(name: String) = updateProfile(
        UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
    )

    suspend fun updatePhoto(photo: Uri) = updateProfile(
        UserProfileChangeRequest.Builder()
            .setPhotoUri(photo)
            .build()
    )

    fun signOut() = auth.signOut()

    suspend fun deleteAccount(
    ) = suspendCoroutine<RequestResult> { cont ->
        currentUser
            ?.delete()
            ?.handleResult(cont)
    }

    private suspend fun updateProfile(
        request: UserProfileChangeRequest
    ) = suspendCoroutine<RequestResult> { cont ->
        currentUser
            ?.updateProfile(request)
            ?.handleResult(cont)
    }

    private fun Task<Void>.handleResult(cont: Continuation<RequestResult>) =
        cont.resume(if (isSuccessful) {
            Success
        } else {
            Fail(exception?.message)
        })

    sealed class RequestResult {
        object Success : RequestResult()
        data class Fail(val error: String?) : RequestResult()
    }
}