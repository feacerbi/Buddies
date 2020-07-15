package com.buddies.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToLogin
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToPetProfile
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
            is OpenPetProfile -> openPetProfile(action.petId)
            is ChangeName -> updateName(action.name)
            is ChangePhoto -> updatePhoto(action.photo)
            is SignOut -> logout()
        }
    }

    private fun openPetProfile(petId: String) {
        updateEffect(Navigate(ProfileToPetProfile(petId)))
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
        updateEffect(Navigate(ProfileToLogin))
        profileUseCases.logout()
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object SignOut : Action()
        data class ChangeName(val name: String) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class OpenPetProfile(val petId: String) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
