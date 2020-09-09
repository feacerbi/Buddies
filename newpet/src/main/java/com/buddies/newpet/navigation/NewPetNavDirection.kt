package com.buddies.newpet.navigation

import com.buddies.common.navigation.Navigator.NavDirection

sealed class NewPetNavDirection : NavDirection() {
    object TagScanToAnimalAndBreed : NavDirection()
    object AnimalAndBreedToInfo : NavDirection()
    object InfoToConfirmation : NavDirection()
    object FinishFlow : NavDirection()
}