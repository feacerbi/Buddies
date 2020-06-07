package com.buddies.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.navigation.Navigator.NavDirection.PROFILE_TO_LOGIN
import com.buddies.common.repository.UserRepository
import com.buddies.common.repository.UserRepository.RequestResult
import com.buddies.common.repository.UserRepository.RequestResult.Fail
import com.buddies.common.repository.UserRepository.RequestResult.Success
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import com.buddies.profile.viewstate.ProfileViewState
import com.buddies.profile.viewstate.ProfileViewStateReducer.ShowInfo
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val userRepository: UserRepository
) : StateViewModel<ProfileViewState, ProfileViewEffect>(ProfileViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        updateState(ShowInfo(userRepository.getCurrentUser()))
    }

    fun perform(action: Action) {
        when (action) {
            is ChangeName -> updateName(action.name)
            is ChangePhoto -> updatePhoto(action.photo)
            is SignOut -> signOut()
        }
    }

    private fun updateName(name: String) = safeLaunch {
        val result = userRepository.updateName(name)
        handleResult(result)
    }

    private fun updatePhoto(photo: Uri) = safeLaunch {
        val result = userRepository.updatePhoto(photo)
        handleResult(result)
    }

    private fun signOut() {
        updateEffect(Navigate(PROFILE_TO_LOGIN))
        userRepository.signOut()
    }

    private fun handleResult(result: RequestResult) = when (result) {
        is Success -> updateState(ShowInfo(userRepository.getCurrentUser()))
        is Fail -> updateEffect(ShowError(result.error))
    }

    sealed class Action {
        object SignOut : Action()
        data class ChangeName(val name: String) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
