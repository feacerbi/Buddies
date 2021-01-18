package com.buddies.generator.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewState

data class GeneratorViewState(
    val enableGenerateButton: Boolean = false,
    val enableAddButton: Boolean = false,
    val syncProgress: Boolean = false,
    val generateProgress: Boolean = false,
    val generatedQrTag: Uri? = null,
    val generatedValue: String = "",
    val generatedEncrypted: String = "",
    val enableShareButton: Boolean = false,
    val enableCopyButtons: Boolean = false,
    @StringRes val error: Int? = null
) : ViewState