package com.buddies.generator.viewstate

import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.generator.model.NewTag

sealed class GeneratorViewStateReducer : ViewStateReducer<GeneratorViewState> {

    object ShowSyncProgress : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            syncProgress = true
        )
    }

    data class ShowNewValue(
        val newValue: String
    ) : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            enableGenerateButton = true,
            syncProgress = false,
            error = null
        )
    }

    object ShowGenerateProgress : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            generateProgress = true
        )
    }

    data class ShowGeneratedData(
        val newTag: NewTag
    ) : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            generateProgress = false,
            generatedEncrypted = newTag.encryptedValue,
            generatedQrTag = newTag.qrCode,
            generatedValue = newTag.value,
            enableAddButton = true,
            enableShareButton = true,
            enableCopyButtons = true
        )
    }

    object EnableGenerateButton : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            enableGenerateButton = true
        )
    }

    object DisableGenerateButton : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            enableGenerateButton = false,
            error = null
        )
    }

    data class ShowError(
        @StringRes val message: Int
    ) : GeneratorViewStateReducer() {
        override fun reduce(state: GeneratorViewState) = state.copy(
            error = message,
            syncProgress = false,
            generateProgress = false
        )
    }
}