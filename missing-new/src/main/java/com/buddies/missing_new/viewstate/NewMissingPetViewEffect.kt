package com.buddies.missing_new.viewstate

import com.buddies.common.model.Breed
import com.buddies.common.viewstate.ViewEffect
import com.buddies.missing_new.navigation.NewMissingPetNavDirection

sealed class NewMissingPetViewEffect : ViewEffect {
    object NavigateBack : NewMissingPetViewEffect()
    data class ShowBreeds(val breedsList: List<Breed>?) : NewMissingPetViewEffect()
    data class Navigate(val direction: NewMissingPetNavDirection) : NewMissingPetViewEffect()
    data class ShowError(val error: Int) : NewMissingPetViewEffect()
}