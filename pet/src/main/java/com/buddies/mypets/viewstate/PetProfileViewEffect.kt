package com.buddies.mypets.viewstate

import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class PetProfileViewEffect : ViewEffect {
    data class ShowAnimalsList(val list: List<Animal>?) : PetProfileViewEffect()
    data class ShowBreedsList(val list: List<Breed>?, val animal: Animal) : PetProfileViewEffect()
    data class Navigate(val direction: NavDirection) : PetProfileViewEffect()
    data class ShowError(val error: Int) : PetProfileViewEffect()
}