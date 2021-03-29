package com.buddies.newpet.navigation

import com.buddies.common.navigation.Navigator.NavDirection

sealed class NewPetNavDirection : NavDirection() {
    data class StartToTagScan(val tagValue: String) : NewPetNavDirection()
    object StartToAnimalAndBreed : NewPetNavDirection()

    object TagScanToAnimalAndBreed : NewPetNavDirection()
    object AnimalAndBreedToInfo : NewPetNavDirection()
    object InfoToConfirmation : NewPetNavDirection()
    object FinishFlow : NewPetNavDirection()
}