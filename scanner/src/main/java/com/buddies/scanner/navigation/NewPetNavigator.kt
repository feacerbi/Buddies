package com.buddies.scanner.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.UnsupportedDirectionException
import com.buddies.scanner.NewPetNavGraphDirections
import com.buddies.scanner.navigation.NewPetNavDirection.*
import com.buddies.scanner.ui.ChooseAnimalBreedFragmentDirections.Companion.actionChooseAnimalBreedFragmentToPetInfoFragment
import com.buddies.scanner.ui.PetInfoFragmentDirections.Companion.actionPetInfoFragmentToAddConfirmationFragment
import com.buddies.scanner.ui.TagScanFragmentDirections.Companion.actionTagScanFragmentToChooseAnimalBreedFragment

class NewPetNavigator : Navigator {

    override fun steer(currentFragment: Fragment, direction: NavDirection) {
        currentFragment.findNavController().navigate(direction.action())
    }

    override fun back(currentFragment: Fragment) {
        currentFragment.findNavController().popBackStack()
    }

    private fun NavDirection.action(): NavDirections = when (this) {
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