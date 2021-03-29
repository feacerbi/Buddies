package com.buddies.newpet.navigation

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.UnsupportedDirectionException
import com.buddies.newpet.NewPetNavGraphDirections
import com.buddies.newpet.navigation.NewPetNavDirection.AnimalAndBreedToInfo
import com.buddies.newpet.navigation.NewPetNavDirection.FinishFlow
import com.buddies.newpet.navigation.NewPetNavDirection.InfoToConfirmation
import com.buddies.newpet.navigation.NewPetNavDirection.StartToAnimalAndBreed
import com.buddies.newpet.navigation.NewPetNavDirection.StartToTagScan
import com.buddies.newpet.navigation.NewPetNavDirection.TagScanToAnimalAndBreed
import com.buddies.newpet.ui.fragment.ChooseAnimalBreedFragmentDirections.Companion.actionChooseAnimalBreedFragmentToPetInfoFragment
import com.buddies.newpet.ui.fragment.PetInfoFragmentDirections.Companion.actionPetInfoFragmentToAddConfirmationFragment
import com.buddies.newpet.ui.fragment.StartFlowFragmentDirections.Companion.actionStartFlowFragmentToChooseAnimalBreedFragment
import com.buddies.newpet.ui.fragment.StartFlowFragmentDirections.Companion.actionStartFlowFragmentToScanTagFragment
import com.buddies.newpet.ui.fragment.TagScanFragmentDirections.Companion.actionTagScanFragmentToChooseAnimalBreedFragment

class NewPetNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is StartToTagScan -> actionStartFlowFragmentToScanTagFragment(tagValue)
        is StartToAnimalAndBreed -> actionStartFlowFragmentToChooseAnimalBreedFragment()
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