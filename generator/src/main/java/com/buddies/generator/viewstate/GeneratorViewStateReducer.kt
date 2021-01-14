package com.buddies.generator.viewstate

import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.generator.model.NewTag

sealed class GeneratorViewStateReducer : ViewStateReducer<GeneratorViewState> {

    object ShowSyncProgress : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            syncProgress = true
        }
    }

    data class ShowNewValue(
        val newValue: String
    ) : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            enableGenerateButton = true
            syncProgress = false
            error = null
        }
    }

    object ShowGenerateProgress : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            generateProgress = true
        }
    }

    data class ShowGeneratedData(
        val newTag: NewTag
    ) : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            generateProgress = false
            generatedEncrypted = newTag.encryptedValue
            generatedQrTag = newTag.qrCode
            generatedValue = newTag.value
            enableAddButton = true
            enableShareButton = true
            enableCopyButtons = true
        }
    }

    object EnableGenerateButton : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            enableGenerateButton = true
        }
    }

    object DisableGenerateButton : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            enableGenerateButton = false
            error = null
        }
    }

    data class ShowError(
        @StringRes val message: Int
    ) : GeneratorViewStateReducer() {
        override val reduce: GeneratorViewState.() -> Unit = {
            error = message
            syncProgress = false
            generateProgress = false
        }
    }
}