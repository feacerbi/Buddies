package com.buddies.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.PROFILE_TO_LOGIN
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.profile.usecase.ProfileUseCases
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import com.buddies.profile.viewstate.ProfileViewState
import com.buddies.profile.viewstate.ProfileViewStateReducer.ShowInfo
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val profileUseCases: ProfileUseCases
) : StateViewModel<ProfileViewState, ProfileViewEffect>(ProfileViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        refreshUser()
    }

    fun perform(action: Action) {
        when (action) {
            is ChangeName -> updateName(action.name)
            is ChangePhoto -> updatePhoto(action.photo)
            is SignOut -> logout()
        }
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        profileUseCases.updateName(name)
        refreshUser()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        profileUseCases.updatePhoto(photo)
        refreshUser()
    }

    private fun refreshUser() = safeLaunch(::showError) {
        val currentUser = profileUseCases.getCurrentUser()
        updateState(ShowInfo(currentUser))
    }

    private fun logout() {
        updateEffect(Navigate(PROFILE_TO_LOGIN))
        profileUseCases.logout()
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object SignOut : Action()
        data class ChangeName(val name: String) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
