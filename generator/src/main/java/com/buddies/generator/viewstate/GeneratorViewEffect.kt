package com.buddies.generator.viewstate

import android.content.Intent
import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewEffect

sealed class GeneratorViewEffect : ViewEffect {
    data class SetNewValue(val value: String) : GeneratorViewEffect()
    data class ShareImage(val intent: Intent) : GeneratorViewEffect()
    data class ShowMessage(@StringRes val message: Int) : GeneratorViewEffect()
}