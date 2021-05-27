package com.buddies.missing_profile.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.User
import com.buddies.common.util.isNotNullNorBlank
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.missing_profile.R
import kotlin.contracts.ExperimentalContracts

sealed class MissingPetViewStateReducer : ViewStateReducer<MissingPetViewState> {

    @ExperimentalContracts
    data class ShowInfo(
        val pet: MissingPet?,
        val animalAndBreed: Pair<Animal, Breed>?,
        val reporter: User?,
        val currentUser: User?
    ) : MissingPetViewStateReducer() {
        override fun reduce(state: MissingPetViewState) = state.copy(
            name = pet?.info?.name ?: "",
            nameEdit = currentUser?.id == pet?.info?.reporter,
            animal = animalAndBreed?.first?.animalInfo?.name ?: "",
            animalEdit = currentUser?.id == pet?.info?.reporter,
            breed = animalAndBreed?.second?.breedInfo?.name ?: "",
            photo = pet?.info?.photo?.toUri() ?: Uri.EMPTY,
            description = pet?.info?.description ?: "",
            showDescription = pet?.info?.description.isNotNullNorBlank(),
            typeDescription = MissingType.fromName(pet?.info?.type ?: "").description,
            reporter = reporter?.info?.name ?: "",
            markAsButton = currentUser?.id == pet?.info?.reporter,
            markAsButtonText = when (pet?.info?.type) {
                FOUND.name -> R.string.mark_as_returned_button_text
                else -> R.string.mark_as_found_button_text
            },
            contactInfo = pet?.info?.reporterInfo?.isNotEmpty() ?: false,
            contactInfoIcon = if (currentUser?.id == pet?.info?.reporter) {
                R.drawable.ic_edit
            } else {
                R.drawable.ic_baseline_info
            },
            toolbarMenu = if (currentUser?.id == pet?.info?.reporter) {
                R.menu.missing_pet_profile_toolbar_menu_reporter
            } else {
                R.menu.missing_pet_profile_toolbar_menu
            },
            loading = false
        )
    }

    object ShowLoading : MissingPetViewStateReducer() {
        override fun reduce(state: MissingPetViewState) = state.copy(
            loading = true
        )
    }

    object HideLoading : MissingPetViewStateReducer() {
        override fun reduce(state: MissingPetViewState) = state.copy(
            loading = false
        )
    }
}