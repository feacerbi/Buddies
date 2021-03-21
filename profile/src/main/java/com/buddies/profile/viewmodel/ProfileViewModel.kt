package com.buddies.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.buddies.common.model.DefaultError
import com.buddies.common.model.InviteNotification
import com.buddies.common.model.PetFavorite
import com.buddies.common.model.PetFoundNotification
import com.buddies.common.model.UserNotification
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToLogin
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToPetProfile
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.configuration.Configuration
import com.buddies.configuration.Feature.MY_PETS
import com.buddies.profile.usecase.ProfileUseCases
import com.buddies.profile.util.toContactInfo
import com.buddies.profile.viewmodel.ProfileViewModel.Action.AcceptNotification
import com.buddies.profile.viewmodel.ProfileViewModel.Action.AddFavorite
import com.buddies.profile.viewmodel.ProfileViewModel.Action.ChangeName
import com.buddies.profile.viewmodel.ProfileViewModel.Action.ChangePhoto
import com.buddies.profile.viewmodel.ProfileViewModel.Action.IgnoreNotification
import com.buddies.profile.viewmodel.ProfileViewModel.Action.NotificationIconClick
import com.buddies.profile.viewmodel.ProfileViewModel.Action.NotificationInfoClick
import com.buddies.profile.viewmodel.ProfileViewModel.Action.OpenNewPetFlow
import com.buddies.profile.viewmodel.ProfileViewModel.Action.OpenPetProfile
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RefreshFavorites
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RefreshInfo
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RefreshNotifications
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RemoveFavorite
import com.buddies.profile.viewmodel.ProfileViewModel.Action.SignOut
import com.buddies.profile.viewstate.ProfileViewEffect
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.RefreshPets
import com.buddies.profile.viewstate.ProfileViewEffect.ShowContactInfoBottomSheet
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import com.buddies.profile.viewstate.ProfileViewState
import com.buddies.profile.viewstate.ProfileViewStateReducer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class ProfileViewModel(
    private val profileUseCases: ProfileUseCases,
    private val configuration: Configuration
) : StateViewModel<ProfileViewState, ProfileViewEffect>(ProfileViewState()), CoroutineScope {

    init {
        refreshUser()
        refreshFavorites()
        refreshNotifications()
    }

    fun perform(action: Action) {
        when (action) {
            is RefreshInfo -> refreshUser()
            is RefreshNotifications -> refreshNotifications()
            is RefreshFavorites -> refreshFavorites()
            is OpenPetProfile -> openPetProfile(action.petId)
            is OpenNewPetFlow -> openNewPetFlow()
            is ChangeName -> updateName(action.name)
            is ChangePhoto -> updatePhoto(action.photo)
            is IgnoreNotification -> ignoreNotification(action.notification)
            is AcceptNotification -> acceptNotification(action.notification)
            is NotificationIconClick -> handleIconClick(action.notification)
            is NotificationInfoClick -> handleShowInfoClick(action.notification)
            is AddFavorite -> addFavoritePet(action.favorite)
            is RemoveFavorite -> removeFavoritePet(action.favorite)
            is SignOut -> logout()
        }
    }

    private fun handleIconClick(notification: UserNotification) {
        when (notification) {
            is InviteNotification -> openPetProfile(notification.pet.id)
            is PetFoundNotification -> openPetProfile(notification.pet.id)
        }
    }

    private fun openPetProfile(petId: String) {
        updateEffect(Navigate(ProfileToPetProfile(petId)))
    }

    private fun openNewPetFlow() {
        updateEffect(Navigate(ProfileToNewPetFlow))
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
        val isMyPetsEnabled = configuration.isFeatureEnabled(MY_PETS)
        updateState(ShowInfo(currentUser, isMyPetsEnabled))
        updateEffect(RefreshPets)
    }

    private fun refreshNotifications() = safeLaunch(::showError) {
        updateState(NotificationsLoading)
        val notifications = profileUseCases.getNotifications()
        updateState(ShowNotifications(notifications))
        updateEffect(RefreshPets)
    }

    private fun refreshFavorites() = safeLaunch(::showError) {
        updateState(FavoritesLoading)

        profileUseCases.listenForUserFavorites()
            .cachedIn(this)
            .collectLatest {
                updateState(ShowFavorites(it))
            }
    }

    private fun addFavoritePet(favorite: PetFavorite) = safeLaunch(::showError) {
        profileUseCases.addFavorite(favorite)
    }

    private fun removeFavoritePet(favorite: PetFavorite) = safeLaunch(::showError) {
        profileUseCases.removeFavorite(favorite)
        refreshFavorites()
    }

    private fun ignoreNotification(notification: UserNotification) = safeLaunch(::showError) {
        updateState(NotificationRemoved(notification))
        profileUseCases.ignoreInvitation(notification.id)
    }

    private fun acceptNotification(notification: InviteNotification) = safeLaunch(::showError) {
        updateState(NotificationRemoved(notification))
        profileUseCases.acceptInvitation(notification.id)
        updateEffect(RefreshPets)
    }

    private fun handleShowInfoClick(notification: PetFoundNotification) {
        updateEffect(ShowContactInfoBottomSheet(notification.shareInfo.toContactInfo()))
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
        object RefreshFavorites : Action()
        object OpenNewPetFlow : Action()
        object SignOut : Action()
        data class ChangeName(val name: String) : Action()
        data class ChangePhoto(val photo: Uri) : Action()
        data class OpenPetProfile(val petId: String) : Action()
        data class NotificationIconClick(val notification: UserNotification) : Action()
        data class IgnoreNotification(val notification: UserNotification) : Action()
        data class NotificationInfoClick(val notification: PetFoundNotification) : Action()
        data class AcceptNotification(val notification: InviteNotification) : Action()
        data class AddFavorite(val favorite: PetFavorite) : Action()
        data class RemoveFavorite(val favorite: PetFavorite) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
