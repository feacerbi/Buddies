package com.buddies.newpet.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewState
import com.buddies.newpet.R

data class NewPetViewState(
    val step: Int = 1,
    val forwardButtonExpanded: Boolean = true,
    val forwardButtonEnabled: Boolean = false,
    @StringRes val result: Int = R.string.empty,
    @StringRes val forwardButtonText: Int = R.string.no_valid_tags_button_message,
    val animalsList: List<Animal> = emptyList(),
    val animalPhoto: Uri = Uri.EMPTY,
    val showCameraOverlay: Boolean = false,
    @StringRes val confirmationTitle: Int = R.string.empty,
    val confirmationLoading: Boolean = false,
    val animalName: String = "",
    val hideAnimalPhoto: Boolean = false,
    val showBackButton: Boolean = false
) : ViewState