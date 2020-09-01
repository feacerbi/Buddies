package com.buddies.scanner.viewstate

import android.net.Uri
import com.buddies.common.model.Animal
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.scanner.R

sealed class NewPetViewStateReducer : ViewStateReducer<NewPetViewState> {

    object ShowScan: NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            title = R.string.new_buddy_flow_title
            step = 1
            result = R.string.scan_your_tag_result
            isLoading = false
            showScanButton = false
            forwardButtonEnabled = false
            forwardButtonExpanded = true
            forwardButtonText = R.string.no_valid_tags_button_message
        }
    }

    object ShowValidating : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.validating_result
            isLoading = true
            showScanButton = false
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowValidated : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.validated_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = true
            forwardButtonExpanded = false
        }
    }

    object ShowInvalid : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.invalid_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowUnrecognized : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            result = R.string.unrecognized_result
            isLoading = false
            showScanButton = true
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    data class ShowAnimalPicker(
        val animals: List<Animal>?
    ) : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            title = R.string.new_buddy_flow_title
            step = 2
            animalsList = animals ?: emptyList()
            forwardButtonEnabled = false
            forwardButtonExpanded = true
            forwardButtonText = R.string.no_animal_and_breed_message
        }
    }

    object ShowBreedPicker : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowAnimalAndBreedPicked : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            forwardButtonEnabled = true
            forwardButtonExpanded = false
        }
    }

    object ShowInfo : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            title = R.string.new_buddy_flow_title
            step = 3
            forwardButtonEnabled = false
            forwardButtonExpanded = true
            forwardButtonText = R.string.no_name_message
            showCameraOverlay = true
        }
    }

    object ShowInvalidInfo : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            forwardButtonEnabled = false
            forwardButtonExpanded = true
        }
    }

    object ShowInfoValidated : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            forwardButtonEnabled = true
            forwardButtonExpanded = false
        }
    }

    data class ShowPetPhoto(
        val photoUri: Uri
    ) : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            animalPhoto = photoUri
            showCameraOverlay = false
        }
    }

    object ShowAddingPet : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            confirmationTitle = R.string.adding_pet_message
            confirmationLoading = true
            hideAnimalPhoto = true
            showBackButton = true
        }
    }

    data class ShowPetConfirmation(
        val name: String
    ) : NewPetViewStateReducer() {
        override val reduce: NewPetViewState.() -> Unit = {
            confirmationTitle = R.string.pet_added_message
            animalName = name
            confirmationLoading = false
            hideAnimalPhoto = false
            showBackButton = false
        }
    }
}