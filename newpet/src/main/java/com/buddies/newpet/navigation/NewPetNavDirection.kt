package com.buddies.newpet.navigation

import com.buddies.common.navigation.Navigator.NavDirection

sealed class NewPetNavDirection : NavDirection() {
    object TagScanToInfo : NewPetNavDirection()

    object InfoToAnimalAndBreed : NewPetNavDirection()

    object AnimalAndBreedToConfirmation : NewPetNavDirection()

    object FinishFlow : NewPetNavDirection()
}