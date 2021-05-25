package com.buddies.missing_new.navigation

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.UnsupportedDirectionException
import com.buddies.missing_new.NewMissingPetNavGraphDirections.Companion.actionGlobalFinish
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.AnimalAndBreedToShareInfo
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.FinishFlow
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.InfoToAnimalAndBreed
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.ShareInfoToConfirmation
import com.buddies.missing_new.navigation.NewMissingPetNavDirection.TypeToInfo
import com.buddies.missing_new.ui.fragment.NewMissingPetChooseAnimalBreedFragmentDirections.Companion.actionMissingPetChooseAnimalBreedFragmentToNewMissingPetShareInfoFragment
import com.buddies.missing_new.ui.fragment.NewMissingPetInfoFragmentDirections.Companion.actionNewMissingPetInfoFragmentToNewMissingPetChooseAnimalBreedFragment
import com.buddies.missing_new.ui.fragment.NewMissingPetShareInfoFragmentDirections.Companion.actionNewMissingPetShareInfoFragmentToNewMissingPetAddConfirmationFragment
import com.buddies.missing_new.ui.fragment.NewMissingPetTypeFragmentDirections.Companion.actionNewMissingPetTypeFragmentToNewMissingPetInfoFragment

class NewMissingPetNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is TypeToInfo ->  actionNewMissingPetTypeFragmentToNewMissingPetInfoFragment()

        is InfoToAnimalAndBreed -> actionNewMissingPetInfoFragmentToNewMissingPetChooseAnimalBreedFragment()

        is AnimalAndBreedToShareInfo -> actionMissingPetChooseAnimalBreedFragmentToNewMissingPetShareInfoFragment()

        is ShareInfoToConfirmation -> actionNewMissingPetShareInfoFragmentToNewMissingPetAddConfirmationFragment()

        is FinishFlow -> actionGlobalFinish()

        else -> throw UnsupportedDirectionException()
    }

    companion object {
        const val NEW_MISSING_PET_NAVIGATOR_NAME = "NewMissingPetNavigator"
    }
}