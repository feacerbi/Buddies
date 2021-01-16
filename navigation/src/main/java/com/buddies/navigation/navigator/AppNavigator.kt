package com.buddies.navigation.navigator

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.NavDirection.GalleryToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.HomeToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.HomeToPetProfile
import com.buddies.common.navigation.Navigator.NavDirection.HomeToProfile
import com.buddies.common.navigation.Navigator.NavDirection.LoginToHome
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToGallery
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToLogin
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToPetProfile
import com.buddies.common.navigation.Navigator.NavDirection.SplashToHome
import com.buddies.common.navigation.Navigator.NavDirection.SplashToLogin
import com.buddies.gallery.ui.fragment.GalleryFragmentDirections.Companion.actionGalleryFragmentToFullscreenFragment
import com.buddies.home.ui.HomeFragmentDirections.Companion.actionHomeFragmentToNewPetFlow
import com.buddies.home.ui.HomeFragmentDirections.Companion.actionHomeFragmentToPetProfileFragment
import com.buddies.home.ui.HomeFragmentDirections.Companion.actionHomeFragmentToProfileFragment
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToHomeFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToHomeFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.pet.ui.fragment.PetProfileFragmentDirections.Companion.actionPetProfileFragmentToFullscreenFragment
import com.buddies.pet.ui.fragment.PetProfileFragmentDirections.Companion.actionPetProfileFragmentToGalleryFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToFullscreenFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToNewPetFlow
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToPetProfileFragment

class AppNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is SplashToLogin -> actionSplashScreenFragmentToLoginFragment()
        is SplashToHome -> actionSplashScreenFragmentToHomeFragment()

        is LoginToHome -> actionLoginFragmentToHomeFragment()

        is HomeToProfile -> actionHomeFragmentToProfileFragment()
        is HomeToPetProfile -> actionHomeFragmentToPetProfileFragment(petId)
        is HomeToNewPetFlow -> actionHomeFragmentToNewPetFlow(tagValue)

        is ProfileToLogin -> actionProfileFragmentToLoginFragment()
        is ProfileToNewPetFlow -> actionProfileFragmentToNewPetFlow()
        is ProfileToPetProfile -> actionProfileFragmentToPetProfileFragment(petId)
        is ProfileToFullscreen -> actionProfileFragmentToFullscreenFragment(
            pictureUrl,
            transitionName,
            false)

        is PetProfileToGallery -> actionPetProfileFragmentToGalleryFragment(petId)
        is PetProfileToFullscreen -> actionPetProfileFragmentToFullscreenFragment(
            pictureUrl,
            transitionName,
            false)

        is GalleryToFullscreen -> actionGalleryFragmentToFullscreenFragment(
            pictureUrl,
            transitionName,
            true)

        else -> throw Navigator.UnsupportedDirectionException()
    }
}