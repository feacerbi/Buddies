package com.buddies.missing_profile.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.MissingPet
import com.buddies.common.model.User
import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.missing_profile.R

sealed class MissingPetViewStateReducer : ViewStateReducer<MissingPetViewState> {

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
            reporter = reporter?.info?.name ?: "",
            toolbarMenu = if (currentUser?.id == pet?.info?.reporter) {
                R.menu.missing_pet_profile_toolbar_menu
            } else {
                R.menu.empty_menu
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