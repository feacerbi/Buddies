package com.buddies.newpet.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.R
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewState

class NewPetViewState(
    @StringRes var title: Int = R.string.empty,
    var step: Int = 0,
    var forwardButtonExpanded: Boolean = false,
    var forwardButtonEnabled: Boolean = false,
    @StringRes var result: Int = R.string.empty,
    @StringRes var forwardButtonText: Int = R.string.empty,
    var animalsList: List<Animal> = emptyList(),
    var animalPhoto: Uri = Uri.EMPTY,
    var showCameraOverlay: Boolean = false,
    @StringRes var confirmationTitle: Int = R.string.empty,
    var confirmationLoading: Boolean = false,
    var animalName: String = "",
    var hideAnimalPhoto: Boolean = false,
    var showBackButton: Boolean = false
) : ViewState