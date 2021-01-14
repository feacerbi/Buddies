package com.buddies.generator.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewState

class GeneratorViewState(
    var enableGenerateButton: Boolean = false,
    var enableAddButton: Boolean = false,
    var syncProgress: Boolean = false,
    var generateProgress: Boolean = false,
    var generatedQrTag: Uri? = null,
    var generatedValue: String = "",
    var generatedEncrypted: String = "",
    @StringRes var error: Int? = null
) : ViewState