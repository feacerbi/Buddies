package com.buddies.missing_new.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewState
import com.buddies.contact.model.ShareInfoField
import com.buddies.missing_new.R

data class NewMissingPetViewState(
    @StringRes val flowTitle: Int = R.string.empty,
    val stepIcons: List<Int> = listOf(
        R.drawable.ic_baseline_assignment,
        R.drawable.ic_baseline_pets,
        R.drawable.ic_baseline_contact_page),
    val step: Int = 0,
    val forwardButtonExpanded: Boolean = true,
    val forwardButtonEnabled: Boolean = false,
    val showBack: Boolean = false,
    val showName: Boolean = true,
    @StringRes val forwardButtonText: Int = R.string.no_name_message,
    val animalsList: List<Animal> = emptyList(),
    val animalPhoto: Uri = Uri.EMPTY,
    val showCameraOverlay: Boolean = true,
    @StringRes val confirmationTitle: Int = R.string.empty,
    val confirmationLoading: Boolean = false,
    val animalName: String = "",
    val hideAnimalPhoto: Boolean = false,
    val shareInfoFields: List<ShareInfoField> = emptyList()
) : ViewState