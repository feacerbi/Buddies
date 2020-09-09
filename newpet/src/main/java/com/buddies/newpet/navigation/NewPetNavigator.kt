package com.buddies.newpet.navigation

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.UnsupportedDirectionException
import com.buddies.newpet.NewPetNavGraphDirections
import com.buddies.newpet.navigation.NewPetNavDirection.*
import com.buddies.newpet.ui.ChooseAnimalBreedFragmentDirections.Companion.actionChooseAnimalBreedFragmentToPetInfoFragment
import com.buddies.newpet.ui.PetInfoFragmentDirections.Companion.actionPetInfoFragmentToAddConfirmationFragment
import com.buddies.newpet.ui.TagScanFragmentDirections.Companion.actionTagScanFragmentToChooseAnimalBreedFragment

class NewPetNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is TagScanToAnimalAndBreed -> actionTagScanFragmentToChooseAnimalBreedFragment()
        is AnimalAndBreedToInfo -> actionChooseAnimalBreedFragmentToPetInfoFragment()
        is InfoToConfirmation -> actionPetInfoFragmentToAddConfirmationFragment()
        is FinishFlow -> NewPetNavGraphDirections.actionGlobalFinish()
        else -> throw UnsupportedDirectionException()
    }

    companion object {
        const val NEW_PET_NAVIGATOR_NAME = "NewPetNavigator"
    }
}