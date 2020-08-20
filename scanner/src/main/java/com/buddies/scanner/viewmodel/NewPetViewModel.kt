package com.buddies.scanner.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.scanner.usecase.NewPetUseCases
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.ScanAgain
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.ValidateTag
import com.buddies.scanner.viewstate.NewPetViewEffect
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import com.buddies.scanner.viewstate.NewPetViewState
import com.buddies.scanner.viewstate.NewPetViewStateReducer.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class NewPetViewModel(
    private val newPetUseCases: NewPetUseCases
) : StateViewModel<NewPetViewState, NewPetViewEffect>(NewPetViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    init {
        startScan()
    }

    fun perform(action: Action) {
        when (action) {
            is ValidateTag -> validateTag(action.result)
            is ScanAgain -> startScan()
        }
    }

    private fun validateTag(number: Result<String>) = safeLaunch(::showError) {
        updateEffect(StopCamera)
        updateState(ShowValidating)

        when (number) {
            is Success -> {
                val tagValue = number.data ?: ""

                when (newPetUseCases.validateTag(tagValue)) {
                    true -> updateState(ShowValidated)
                    false -> updateState(ShowInvalid)
                }
            }
            is Fail -> updateState(ShowUnrecognized)
        }
    }

    private fun startScan() {
        updateState(ShowScan)
        updateEffect(StartCamera)
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
        startScan()
    }

    sealed class Action {
        data class ValidateTag(val result: Result<String>) : Action()
        object ScanAgain : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext
}
