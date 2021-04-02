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
import com.buddies.common.navigation.Navigator.NavDirection.LoginToMissingFeed
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToAllMissingPets
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.MissingFeedToProfile
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToGallery
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToLogin
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToNewPetFlow
import com.buddies.common.navigation.Navigator.NavDirection.ProfileToPetProfile
import com.buddies.common.navigation.Navigator.NavDirection.SplashToHome
import com.buddies.common.navigation.Navigator.NavDirection.SplashToLogin
import com.buddies.common.navigation.Navigator.NavDirection.SplashToMissingFeed
import com.buddies.gallery.ui.fragment.GalleryFragmentDirections.Companion.actionGalleryFragmentToFullscreenFragment
import com.buddies.home.ui.fragment.HomeFragmentDirections.Companion.actionHomeFragmentToNewPetFlow
import com.buddies.home.ui.fragment.HomeFragmentDirections.Companion.actionHomeFragmentToPetProfileFragment
import com.buddies.home.ui.fragment.HomeFragmentDirections.Companion.actionHomeFragmentToProfileFragment
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToHomeFragment
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToMissingFeedFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToHomeFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToMissingFeedFragment
import com.buddies.missing.ui.MissingFeedFragmentDirections.Companion.actionMissingFeedFragmentToAllMissingPetsFragment
import com.buddies.missing.ui.MissingFeedFragmentDirections.Companion.actionMissingFeedFragmentToNewPetNavGraph
import com.buddies.missing.ui.MissingFeedFragmentDirections.Companion.actionMissingFeedFragmentToProfileFragment
import com.buddies.newpet.util.FlowType
import com.buddies.pet.ui.fragment.PetProfileFragmentDirections.Companion.actionPetProfileFragmentToFullscreenFragment
import com.buddies.pet.ui.fragment.PetProfileFragmentDirections.Companion.actionPetProfileFragmentToGalleryFragment
import com.buddies.profile.ui.fragment.ProfileFragmentDirections.Companion.actionProfileFragmentToFullscreenFragment
import com.buddies.profile.ui.fragment.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.profile.ui.fragment.ProfileFragmentDirections.Companion.actionProfileFragmentToNewPetFlow
import com.buddies.profile.ui.fragment.ProfileFragmentDirections.Companion.actionProfileFragmentToPetProfileFragment

class AppNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is SplashToLogin -> actionSplashScreenFragmentToLoginFragment()
        is SplashToHome -> actionSplashScreenFragmentToHomeFragment()
        is SplashToMissingFeed -> actionSplashScreenFragmentToMissingFeedFragment()

        is LoginToHome -> actionLoginFragmentToHomeFragment()
        is LoginToMissingFeed -> actionLoginFragmentToMissingFeedFragment()

        is HomeToProfile -> actionHomeFragmentToProfileFragment()
        is HomeToPetProfile -> actionHomeFragmentToPetProfileFragment(petId)
        is HomeToNewPetFlow -> actionHomeFragmentToNewPetFlow(FlowType.TAG, tagValue)

        is MissingFeedToProfile -> actionMissingFeedFragmentToProfileFragment()
        is MissingFeedToNewPetFlow -> actionMissingFeedFragmentToNewPetNavGraph(FlowType.MISSING)
        is MissingFeedToAllMissingPets -> actionMissingFeedFragmentToAllMissingPetsFragment()

        is ProfileToLogin -> actionProfileFragmentToLoginFragment()
        is ProfileToNewPetFlow -> actionProfileFragmentToNewPetFlow(FlowType.TAG)
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