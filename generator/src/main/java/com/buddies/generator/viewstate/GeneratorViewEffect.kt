package com.buddies.generator.viewstate

import androidx.annotation.StringRes
import com.buddies.common.viewstate.ViewEffect

sealed class GeneratorViewEffect : ViewEffect {
    data class SetNewValue(val value: String) : GeneratorViewEffect()
    data class ShowMessage(@StringRes val message: Int) : GeneratorViewEffect()
}