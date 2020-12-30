package com.buddies.pet.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class PetProfileViewEffect : ViewEffect {
    data class ShowAnimalsList(val list: List<Animal>?) : PetProfileViewEffect()
    data class ShowBreedsList(val list: List<Breed>?, val animal: Animal) : PetProfileViewEffect()
    data class ShowBottomMessage(
        @StringRes val message: Int,
        val params: Array<String> = arrayOf()) : PetProfileViewEffect()
    data class ShowConfirmDialog(
        @StringRes val message: Int,
        val name: String) : PetProfileViewEffect()
    data class Navigate(val direction: NavDirection) : PetProfileViewEffect()
    data class ShowError(val error: Int) : PetProfileViewEffect()
}