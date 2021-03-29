package com.buddies.newpet.viewstate

import com.buddies.common.model.Breed
import com.buddies.common.viewstate.ViewEffect
import com.buddies.newpet.navigation.NewPetNavDirection

sealed class NewPetViewEffect : ViewEffect {
    object NavigateBack : NewPetViewEffect()
    data class ShowBreeds(val breedsList: List<Breed>?) : NewPetViewEffect()
    data class Navigate(val direction: NewPetNavDirection) : NewPetViewEffect()
    data class ShowError(val error: Int) : NewPetViewEffect()
}