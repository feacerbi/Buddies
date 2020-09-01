package com.buddies.scanner.viewstate

import android.net.Uri
import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewState

class NewPetViewState(
    @StringRes var title: Int = -1,
    var step: Int = 0,
    @StringRes var result: Int = -1,
    var isLoading: Boolean = false,
    var showScanButton: Boolean = false,
    var forwardButtonExpanded: Boolean = false,
    var forwardButtonEnabled: Boolean = false,
    @StringRes var forwardButtonText: Int = -1,
    var animalsList: List<Animal> = emptyList(),
    var animalPhoto: Uri = Uri.EMPTY,
    var showCameraOverlay: Boolean = false,
    @StringRes var confirmationTitle: Int = -1,
    var confirmationLoading: Boolean = false,
    var animalName: String = "",
    var hideAnimalPhoto: Boolean = false,
    var showBackButton: Boolean = false
) : ViewState