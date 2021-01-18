package com.buddies.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.Tag
import com.buddies.common.model.UserInfo
import com.buddies.common.navigation.Navigator.NavDirection.HomeToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.HomeToPetProfile
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.home.R
import com.buddies.home.model.ShareInfo
import com.buddies.home.usecase.HomeUseCases
import com.buddies.home.viewmodel.HomeViewModel.Action.AddNewPet
import com.buddies.home.viewmodel.HomeViewModel.Action.CloseScanner
import com.buddies.home.viewmodel.HomeViewModel.Action.HandleTag
import com.buddies.home.viewmodel.HomeViewModel.Action.NotifyPetFound
import com.buddies.home.viewmodel.HomeViewModel.Action.OpenPetProfile
import com.buddies.home.viewmodel.HomeViewModel.Action.ScanPet
import com.buddies.home.viewmodel.HomeViewModel.Action.SendUserInfo
import com.buddies.home.viewstate.HomeViewEffect
import com.buddies.home.viewstate.HomeViewEffect.Navigate
import com.buddies.home.viewstate.HomeViewEffect.ShowAddPetDialog
import com.buddies.home.viewstate.HomeViewEffect.ShowError
import com.buddies.home.viewstate.HomeViewEffect.ShowLostPetDialog
import com.buddies.home.viewstate.HomeViewEffect.ShowMessage
import com.buddies.home.viewstate.HomeViewEffect.ShowPetDialog
import com.buddies.home.viewstate.HomeViewEffect.ShowShareInfoDialog
import com.buddies.home.viewstate.HomeViewEffect.StopPetScanner
import com.buddies.home.viewstate.HomeViewState
import com.buddies.home.viewstate.HomeViewStateReducer.HidePetScanner
import com.buddies.home.viewstate.HomeViewStateReducer.ShowPetScanner
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    private val homeUseCases: HomeUseCases
) : StateViewModel<HomeViewState, HomeViewEffect>(HomeViewState()), CoroutineScope {

    fun perform(action: Action) {
        when (action) {
            is ScanPet -> startPetScanner()
            is HandleTag -> handleTag(action.tag)
            is OpenPetProfile -> openPetProfile(action.petId)
            is NotifyPetFound -> requestUserInfo(action.petId)
            is SendUserInfo -> sendUserInfo(action.petId, action.info)
            is AddNewPet -> openNewPetFlow(action.tag)
            is CloseScanner -> closeScanner()
        }
    }

    private fun startPetScanner() {
        updateState(ShowPetScanner)
    }

    private fun handleTag(
        tag: Tag?
    ) = safeLaunch(::showError) {
        if (tag != null) {
            val pet = homeUseCases.getPet(tag.id)

            when {
                pet == null -> updateEffect(ShowAddPetDialog(tag))
                pet.info.lost -> updateEffect(ShowLostPetDialog(pet))
                else -> updateEffect(ShowPetDialog(pet))
            }
        }
    }

    private fun openPetProfile(petId: String) {
        closeScanner()
        updateEffect(Navigate(HomeToPetProfile(petId)))
    }

    private fun requestUserInfo(petId: String) = safeLaunch(::showError) {
        val userInfo = homeUseCases.getUser() ?: UserInfo()
        updateEffect(ShowShareInfoDialog(petId, userInfo))
    }

    private fun sendUserInfo(petId: String, info: List<ShareInfo>) = safeLaunch(::showError) {
        closeScanner()
        homeUseCases.notifyPetFound(petId, info)
        updateEffect(ShowMessage(R.string.owners_notified_message))
    }

    private fun openNewPetFlow(tag: Tag) {
        closeScanner()
        updateEffect(Navigate(HomeToNewPetFlow(tag.info.value)))
    }

    private fun closeScanner() {
        updateState(HidePetScanner)
        updateEffect(StopPetScanner)
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    sealed class Action {
        object ScanPet : Action()
        data class HandleTag(val tag: Tag?) : Action()
        data class OpenPetProfile(val petId: String) : Action()
        data class SendUserInfo(val petId: String, val info: List<ShareInfo>) : Action()
        data class NotifyPetFound(val petId: String) : Action()
        data class AddNewPet(val tag: Tag) : Action()
        object CloseScanner : Action()
    }
}