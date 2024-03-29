package com.buddies.missing_profile.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.navigation.Navigator
import com.buddies.common.viewstate.ViewEffect
import com.buddies.contact.model.ContactInfo

sealed class MissingPetViewEffect : ViewEffect {
    object NavigateBack : MissingPetViewEffect()
    data class ShowAnimalsList(val list: List<Animal>?) : MissingPetViewEffect()
    data class ShowBreedsList(val list: List<Breed>?, val animal: Animal) : MissingPetViewEffect()
    data class ShowMessage(
        @StringRes val message: Int,
        val params: List<String> = emptyList()) : MissingPetViewEffect()
    data class ShowContactInfoBottomSheet(val info: List<ContactInfo>?) : MissingPetViewEffect()
    data class ShowShareInfoBottomSheet(val info: List<ContactInfo>?) : MissingPetViewEffect()
    data class ShowConfirmRemovalBottomSheet(
        @StringRes val message: Int,
        val petName: String) : MissingPetViewEffect()
    data class ShowConfirmReturnedBottomSheet(
        @StringRes val message: Int,
        val petName: String) : MissingPetViewEffect()
    data class Navigate(val direction: Navigator.NavDirection) : MissingPetViewEffect()
    data class ShowError(val error: Int) : MissingPetViewEffect()
}