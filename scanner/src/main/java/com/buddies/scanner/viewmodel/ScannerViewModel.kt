package com.buddies.scanner.viewmodel

import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.model.ErrorCode.INVALID_TAG
import com.buddies.common.util.TAG_PREFIX
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.scanner.usecase.ScannerUseCases
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.CloseScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.HandleResult
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.StartScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.ValidateScan
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

    private val encrypter by inject(Encrypter::class.java)

    fun perform(action: Action) {
        when (action) {
            is StartScanner -> startPetScanner()
            is CloseScanner -> closeScanner()
            is ValidateScan -> handleScannedTag(action.result)
            is HandleResult -> handleTagResult(action.tagValue)
        }
    }

    private fun startPetScanner() {
        updateState(StartPetScanner)
        updateEffect(StartCamera)
    }

    private fun handleScannedTag(number: String?) = safeLaunch(::showError) {
        updateEffect(StopCamera)
        updateState(ShowValidating)

        try {
            val decodedQRCode = encrypter.decrypt(number?.removePrefix(TAG_PREFIX))
            val tag = scannerUseCases.getTag(decodedQRCode)

            updateState(ShowResult(tag))
        } catch (exception: Exception) {
            updateState(ShowUnrecognized)
        }
    }

    private fun handleTagResult(tagValue: String) = safeLaunch(::showError) {
        if (tagValue.isNotEmpty()) {
            val tag = scannerUseCases.getTag(tagValue)

            updateState(ShowResult(tag))
            updateEffect(StopCamera)
        }
    }

    private fun closeScanner() {
        updateState(StopPetScanner)
        updateEffect(StopCamera)
    }

    private fun showError(error: DefaultError) {
        when (error.code) {
            INVALID_TAG -> updateState(ShowInvalid)
            else -> updateState(ShowError(error.code.message))
        }
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    sealed class Action {
        object StartScanner : Action()
        object CloseScanner : Action()
        data class ValidateScan(val result: String?) : Action()
        data class HandleResult(val tagValue: String) : Action()
    }
}