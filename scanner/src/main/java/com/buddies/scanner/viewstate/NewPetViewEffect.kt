package com.buddies.scanner.viewstate

import com.buddies.common.model.Breed
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class NewPetViewEffect : ViewEffect {
    object StartCamera : NewPetViewEffect()
    object StopCamera: NewPetViewEffect()
    object NavigateBack : NewPetViewEffect()
    data class ShowBreeds(val breedsList: List<Breed>?) : NewPetViewEffect()
    data class Navigate(val direction: NavDirection) : NewPetViewEffect()
    data class ShowError(val error: Int) : NewPetViewEffect()
}