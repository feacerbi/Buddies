package com.buddies.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.UserNotification
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToLogin
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToPetProfile
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.profile.usecase.ProfileUseCases
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.RefreshPets
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import com.buddies.profile.viewstate.ProfileViewState
import com.buddies.profile.viewstate.ProfileViewStateReducer.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val profileUseCases: ProfileUseCases
) : StateViewModel<ProfileViewState, ProfileViewEffect>(ProfileViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        refreshUser()
        refreshNotifications()
    }

    fun perform(action: Action) {
        when (action) {
            is RefreshInfo -> refreshUser()
            is RefreshNotifications -> refreshNotifications()
            is OpenPetProfile -> openPetProfile(action.petId)
            is ChangeName -> updateName(action.name)
            is ChangePhoto -> updatePhoto(action.photo)
            is IgnoreNotification -> ignoreNotification(action.notification)
            is AcceptNotification -> acceptNotification(action.notification)
            is SignOut -> logout()
        }
    }

    private fun openPetProfile(petId: String) {
        updateState(ExpandedWidget)
        updateEffect(Navigate(ProfileToPetProfile(petId)))
    }

    private fun updateName(name: String) = safeLaunch(::showError) {
        updateState(InfoLoading)
        profileUseCases.updateName(name)
        refreshUser()
    }

    private fun updatePhoto(photo: Uri) = safeLaunch(::showError) {
        updateState(InfoLoading)
        profileUseCases.updatePhoto(photo)
        refreshUser()
    }

    private fun refreshUser() = safeLaunch(::showError) {
        updateState(InfoLoading)
        val currentUser = profileUseCases.getCurrentUser()
        updateState(ShowInfo(currentUser))
    }

    private fun refreshNotifications() = safeLaunch(::showError) {
        updateState(NotificationsLoading)
        val notifications = profileUseCases.getNotifications()
        updateState(ShowNotifications(notifications))
    }

    private fun ignoreNotification(notification: UserNotification) = safeLaunch(::showError) {
        updateState(NotificationRemoved(notification))
        profileUseCases.ignoreInvitation(notification.id)
        refreshNotifications()
    }

    private fun acceptNotification(notification: UserNotification) = safeLaunch(::showError) {
        updateState(NotificationRemoved(notification))
        profileUseCases.acceptInvitation(notification.id)
        refreshNotifications()
        updateEffect(RefreshPets)
    }

    private fun logout() {
        updateEffect(Navigate(ProfileToLogin))
        profileUseCases.logout()
    }

    private fun showError(error: DefaultError) {
        updateState(ShowError)
        updateEffect(ShowError(error.code.message))
    }

    sealed class Action {
        object RefreshInfo : Action()
        object RefreshNotifications : Action()
        object SignOut : Action()
        data class ChangeName(val name: String) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class OpenPetProfile(val petId: String) : Action()
        data class IgnoreNotification(val notification: UserNotification) : Action()
        data class AcceptNotification(val notification: UserNotification) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
