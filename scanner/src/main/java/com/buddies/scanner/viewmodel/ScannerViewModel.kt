package com.buddies.scanner.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.ErrorCode.INVALID_TAG
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.scanner.usecase.ScannerUseCases
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.CloseScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.StartScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.ValidateTag
import com.buddies.scanner.viewstate.ScannerViewEffect
import com.buddies.scanner.viewstate.ScannerViewEffect.StartCamera
import com.buddies.scanner.viewstate.ScannerViewEffect.StopCamera
import com.buddies.scanner.viewstate.ScannerViewState
import com.buddies.scanner.viewstate.ScannerViewStateReducer.ShowError
import com.buddies.scanner.viewstate.ScannerViewStateReducer.ShowInvalid
import com.buddies.scanner.viewstate.ScannerViewStateReducer.ShowResult
import com.buddies.scanner.viewstate.ScannerViewStateReducer.ShowUnrecognized
import com.buddies.scanner.viewstate.ScannerViewStateReducer.ShowValidating
import com.buddies.scanner.viewstate.ScannerViewStateReducer.StartPetScanner
import com.buddies.scanner.viewstate.ScannerViewStateReducer.StopPetScanner
import com.buddies.security.encryption.Encrypter
import kotlinx.coroutines.CoroutineScope
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

class ScannerViewModel(
    private val scannerUseCases: ScannerUseCases
) : StateViewModel<ScannerViewState, ScannerViewEffect>(ScannerViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect
    
    private val encrypter by inject(Encrypter::class.java)

    fun perform(action: Action) {
        when (action) {
            is StartScanner -> startPetScanner()
            is CloseScanner -> closeScanner()
            is ValidateTag -> handleTag(action.result)
        }
    }

    private fun startPetScanner() {
        updateState(StartPetScanner)
        updateEffect(StartCamera)
    }

    private fun handleTag(number: String?) = safeLaunch(::showError) {
        updateEffect(StopCamera)
        updateState(ShowValidating)
        
        try {
            val decodedBarcode = encrypter.decrypt(number?.removePrefix(TAG_PREFIX))
            val tag = scannerUseCases.getTag(decodedBarcode)

            updateState(ShowResult(tag))
        } catch (exception: Exception) {
            updateState(ShowUnrecognized)
        }
    }

    private fun closeScanner() {
        updateState(StopPetScanner)
        updateEffect(StopCamera)
    }

    private fun showError(error: DefaultError) {
        when (error.code) {
            INVALID_TAG -> updateState(ShowInvalid)
            else -> updateState(ShowError)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    sealed class Action {
        object StartScanner : Action()
        object CloseScanner : Action()
        data class ValidateTag(val result: String?) : Action()
    }

    companion object {
        private const val TAG_PREFIX = "https://play.google.com/store/apps/details?id=com.buddies&referrer="
    }
}