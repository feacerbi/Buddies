package com.buddies.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.ErrorCode.INVALID_TAG
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.model.UserInfo
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.home.R
import com.buddies.home.model.ShareInfo
import com.buddies.home.usecase.HomeUseCases
import com.buddies.home.viewmodel.HomeViewModel.Action.CloseScanner
import com.buddies.home.viewmodel.HomeViewModel.Action.NotifyPetFound
import com.buddies.home.viewmodel.HomeViewModel.Action.ScanPet
import com.buddies.home.viewmodel.HomeViewModel.Action.SendUserInfo
import com.buddies.home.viewmodel.HomeViewModel.Action.ValidateTag
import com.buddies.home.viewstate.HomeViewEffect
import com.buddies.home.viewstate.HomeViewEffect.ShowError
import com.buddies.home.viewstate.HomeViewEffect.ShowMessage
import com.buddies.home.viewstate.HomeViewEffect.ShowShareInfoDialog
import com.buddies.home.viewstate.HomeViewEffect.StartCamera
import com.buddies.home.viewstate.HomeViewEffect.StopCamera
import com.buddies.home.viewstate.HomeViewState
import com.buddies.home.viewstate.HomeViewStateReducer.IdleHome
import com.buddies.home.viewstate.HomeViewStateReducer.ShowInvalid
import com.buddies.home.viewstate.HomeViewStateReducer.ShowLostPet
import com.buddies.home.viewstate.HomeViewStateReducer.ShowPet
import com.buddies.home.viewstate.HomeViewStateReducer.ShowUnrecognized
import com.buddies.home.viewstate.HomeViewStateReducer.ShowValidating
import com.buddies.home.viewstate.HomeViewStateReducer.StartPetScanner
import com.buddies.home.viewstate.HomeViewStateReducer.StopPetScanner
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    private val homeUseCases: HomeUseCases
) : StateViewModel<HomeViewState, HomeViewEffect>(HomeViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private var petFoundId: String = ""

    init {
        updateState(IdleHome)
    }

    fun perform(action: Action) {
        when (action) {
            is ScanPet -> startPetScanner()
            is ValidateTag -> handleTag(action.result)
            is NotifyPetFound -> requestUserInfo()
            is SendUserInfo -> sendUserInfo(action.info)
            is CloseScanner -> closeScanner()
        }
    }

    private fun startPetScanner() {
        updateState(StartPetScanner)
        updateEffect(StartCamera)
    }

    private fun handleTag(number: Result<String>) {
        updateEffect(StopCamera)
        updateState(ShowValidating)

        when (number) {
            is Success -> {
                val tagValue = number.data ?: ""
                checkLost(tagValue)
            }
            is Fail -> updateState(ShowUnrecognized)
        }
    }

    private fun checkLost(
        tagValue: String
    ) = safeLaunch(::showError) {
        val pet = homeUseCases.getPet(tagValue)

        when (pet?.info?.lost) {
            true -> {
                petFoundId = pet.id
                updateState(ShowLostPet(pet.info.name))
            }
            false -> updateState(ShowPet(pet.info.name))
        }
    }

    private fun requestUserInfo() = safeLaunch(::showError) {
        val userInfo = homeUseCases.getUser() ?: UserInfo()
        updateEffect(ShowShareInfoDialog(userInfo))
    }

    private fun sendUserInfo(info: List<ShareInfo>) = safeLaunch(::showError) {
        //homeUseCases.notifyPetFound(petFoundId)
        updateState(StopPetScanner)
        updateEffect(ShowMessage(R.string.owners_notified_message))
    }

    private fun closeScanner() {
        updateState(StopPetScanner)
        updateEffect(StopCamera)
    }

    private fun showError(error: DefaultError) {
        when (error.code) {
            INVALID_TAG -> updateState(ShowInvalid)
            else -> {
                updateEffect(ShowError(error.code.message))
                startPetScanner()
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    sealed class Action {
        object ScanPet : Action()
        data class ValidateTag(val result: Result<String>) : Action()
        data class SendUserInfo(val info: List<ShareInfo>) : Action()
        object NotifyPetFound : Action()
        object CloseScanner : Action()
    }
}