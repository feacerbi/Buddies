package com.buddies.newpet.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewState
import com.buddies.newpet.R

data class NewPetViewState(
    val stepIcons: List<Int> = listOf(
        R.drawable.ic_baseline_loyalty,
        R.drawable.ic_baseline_assignment,
        R.drawable.ic_baseline_pets),
    val step: Int = 1,
    val forwardButtonExpanded: Boolean = true,
    val forwardButtonEnabled: Boolean = false,
    @StringRes val result: Int = R.string.empty,
    @StringRes val forwardButtonText: Int = R.string.no_valid_tags_button_message,
    val animalsList: List<Animal> = emptyList(),
    val animalPhoto: Uri = Uri.EMPTY,
    val showCameraOverlay: Boolean = true,
    @StringRes val confirmationTitle: Int = R.string.empty,
    val confirmationLoading: Boolean = false,
    val animalName: String = "",
    val hideAnimalPhoto: Boolean = false
) : ViewState