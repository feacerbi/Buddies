package com.buddies.generator.viewmodel

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.buddies.common.model.DefaultError
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.generator.R
import com.buddies.generator.model.NewTag
import com.buddies.generator.usecase.GeneratorUseCases
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.AddToDB
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.CopyToClipboard
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.Generate
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.GenerateNewValue
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.InputChanged
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.ShareQRCode
import com.buddies.generator.viewstate.GeneratorViewEffect
import com.buddies.generator.viewstate.GeneratorViewEffect.SetNewValue
import com.buddies.generator.viewstate.GeneratorViewEffect.ShareImage
import com.buddies.generator.viewstate.GeneratorViewEffect.ShowMessage
import com.buddies.generator.viewstate.GeneratorViewState
import com.buddies.generator.viewstate.GeneratorViewStateReducer.DisableGenerateButton
import com.buddies.generator.viewstate.GeneratorViewStateReducer.EnableGenerateButton
import com.buddies.generator.viewstate.GeneratorViewStateReducer.ShowError
import com.buddies.generator.viewstate.GeneratorViewStateReducer.ShowGenerateProgress
import com.buddies.generator.viewstate.GeneratorViewStateReducer.ShowGeneratedData
import com.buddies.generator.viewstate.GeneratorViewStateReducer.ShowNewValue
import com.buddies.generator.viewstate.GeneratorViewStateReducer.ShowSyncProgress
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class GeneratorViewModel(
    private val generatorUseCases: GeneratorUseCases
) : StateViewModel<GeneratorViewState, GeneratorViewEffect>(GeneratorViewState()), CoroutineScope {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private var newTag = NewTag()

    init {
        tryLogin()
    }

    fun perform(action: Action) {
        when (action) {
            is InputChanged -> handleInputChanged(action.input)
            is GenerateNewValue -> generateValue()
            is Generate -> generateTag(action.input, action.size)
            is AddToDB -> addTagToDB()
            is CopyToClipboard -> copyToClipboard(action.text)
            is ShareQRCode -> shareQRCode()
        }
    }

    private fun tryLogin() = safeLaunch(::showError) {
        val result = generatorUseCases.loginWithAdminCredentials()

        if (result == null || result == false) {
            updateEffect(ShowMessage(R.string.login_fail_message))
        }
    }

    private fun handleInputChanged(input: String) {
        if (input.length == TAG_VALUE_SIZE) {
            updateState(EnableGenerateButton)
        } else {
            updateState(DisableGenerateButton)
        }
    }

    private fun generateValue() = safeLaunch(::showError) {
        updateState(ShowSyncProgress)

        var tagValidated = false
        var newValue = 0L

        while (tagValidated.not()) {
            newValue = Random.nextLong(MIN_RANGE, MAX_RANGE)
            tagValidated = validateTagValue(newValue.toString())
        }

        updateState(ShowNewValue(newValue.toString()))
        updateEffect(SetNewValue(newValue.toString()))
    }

    private fun generateTag(
        input: String,
        size: Int
    ) = safeLaunch(::showError) {
        updateState(ShowGenerateProgress)

        newTag.value = input
        newTag.encryptedValue = generatorUseCases.encryptTagValue(input)
        newTag.qrCode = generatorUseCases.generateQRCode(newTag.encryptedValue, size, size)

        updateState(ShowGeneratedData(newTag))
    }

    private fun addTagToDB() = safeLaunch(::showError) {
        generatorUseCases.addNewTag(newTag)
        updateEffect(ShowMessage(R.string.tag_added_message))
    }

    private fun copyToClipboard(text: String) {
        generatorUseCases.copyToClipboard(text)
        updateEffect(ShowMessage(R.string.copied_to_clipboard_message))
    }

    private fun shareQRCode() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, newTag.qrCode)
            type = IMAGE_MIME_TYPE
        }

        updateEffect(ShareImage(shareIntent))
    }

    private suspend fun validateTagValue(value: String): Boolean {
        val tagValueExists = generatorUseCases.tagValueExists(value)
        return tagValueExists != null && tagValueExists.not()
    }

    private fun showError(error: DefaultError) {
        updateState(ShowError(error.code.message))
    }

    override fun onCleared() {
        super.onCleared()
        generatorUseCases.logoutAdmin()
    }

    sealed class Action {
        data class InputChanged(val input: String) : Action()
        object GenerateNewValue : Action()
        data class Generate(val input: String, val size: Int) : Action()
        object AddToDB : Action()
        data class CopyToClipboard(val text: String) : Action()
        object ShareQRCode : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    companion object {
        private const val TAG_VALUE_SIZE = 16
        private const val MIN_RANGE = 1000000000000000
        private const val MAX_RANGE = 9999999999999999
        private const val IMAGE_MIME_TYPE = "image/jpeg"
    }
}