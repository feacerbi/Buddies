package com.buddies.missing_new.navigation

import com.buddies.common.navigation.Navigator.NavDirection

sealed class NewMissingPetNavDirection : NavDirection() {
    object TypeToInfo : NewMissingPetNavDirection()

    object InfoToAnimalAndBreed : NewMissingPetNavDirection()

    object AnimalAndBreedToShareInfo : NewMissingPetNavDirection()

    object ShareInfoToConfirmation : NewMissingPetNavDirection()

    object FinishFlow : NewMissingPetNavDirection()
}