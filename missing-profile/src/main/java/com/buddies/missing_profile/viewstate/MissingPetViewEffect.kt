package com.buddies.missing_profile.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.navigation.Navigator
import com.buddies.common.viewstate.ViewEffect

sealed class MissingPetViewEffect : ViewEffect {
    data class ShowAnimalsList(val list: List<Animal>?) : MissingPetViewEffect()
    data class ShowBreedsList(val list: List<Breed>?, val animal: Animal) : MissingPetViewEffect()
    data class ShowBottomMessage(
        @StringRes val message: Int,
        val params: List<String> = emptyList()) : MissingPetViewEffect()
    data class Navigate(val direction: Navigator.NavDirection) : MissingPetViewEffect()
    data class ShowError(val error: Int) : MissingPetViewEffect()
}