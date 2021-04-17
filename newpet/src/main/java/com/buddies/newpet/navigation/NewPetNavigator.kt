package com.buddies.newpet.navigation

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.UnsupportedDirectionException
import com.buddies.newpet.NewPetNavGraphDirections.Companion.actionGlobalFinish
import com.buddies.newpet.navigation.NewPetNavDirection.AnimalAndBreedToConfirmation
import com.buddies.newpet.navigation.NewPetNavDirection.FinishFlow
import com.buddies.newpet.navigation.NewPetNavDirection.InfoToAnimalAndBreed
import com.buddies.newpet.navigation.NewPetNavDirection.TagScanToInfo
import com.buddies.newpet.ui.fragment.NewPetChooseAnimalBreedFragmentDirections.Companion.actionNewPetChooseAnimalBreedFragmentToNewPetAddConfirmationFragment
import com.buddies.newpet.ui.fragment.NewPetInfoFragmentDirections.Companion.actionNewPetInfoFragmentToNewPetChooseAnimalBreedFragment
import com.buddies.newpet.ui.fragment.NewPetTagScanFragmentDirections.Companion.actionNewPetTagScanFragmentToNewPetInfoFragment

class NewPetNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is TagScanToInfo -> actionNewPetTagScanFragmentToNewPetInfoFragment()

        is InfoToAnimalAndBreed -> actionNewPetInfoFragmentToNewPetChooseAnimalBreedFragment()

        is AnimalAndBreedToConfirmation -> actionNewPetChooseAnimalBreedFragmentToNewPetAddConfirmationFragment()

        is FinishFlow -> actionGlobalFinish()

        else -> throw UnsupportedDirectionException()
    }

    companion object {
        const val NEW_PET_NAVIGATOR_NAME = "NewPetNavigator"
    }
}