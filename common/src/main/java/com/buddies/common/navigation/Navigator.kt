package com.buddies.common.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator

interface Navigator {

    fun steer(currentFragment: Fragment, direction: NavDirection)
    fun steer(currentFragment: Fragment, direction: NavDirection, extras: FragmentNavigator.Extras)
    fun back(currentFragment: Fragment)

    open class NavDirection {
        object SplashToLogin : NavDirection()
        object SplashToHome : NavDirection()
        object SplashToMissingFeed : NavDirection()

        object LoginToHome : NavDirection()
        object LoginToMissingFeed : NavDirection()

        object HomeToProfile : NavDirection()
        object HomeToSettings : NavDirection()
        data class HomeToPetProfile(val petId: String) : NavDirection()
        data class HomeToNewPetFlow(val tagValue: String) : NavDirection()

        object MissingFeedToProfile : NavDirection()
        object MissingFeedToNewMissingPetFlow : NavDirection()
        object MissingFeedToSettings : NavDirection()
        data class MissingFeedToAllMissingPets(
            val missingType: String
        ) : NavDirection()
        data class MissingFeedToMissingPet(
            val petId: String
        ) : NavDirection()

        data class AllMissingPetsToMissingPet(
            val petId: String
        ) : NavDirection()

        data class MissingPetToGallery(
            val petId: String,
            val editEnabled: Boolean) : NavDirection()
        data class MissingPetToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()

        object ProfileToLogin : NavDirection()
        object ProfileToNewPetFlow : NavDirection()
        data class ProfileToPetProfile(val petId: String) : NavDirection()
        data class ProfileToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()

        data class PetProfileToGallery(
            val petId: String,
            val editEnabled: Boolean) : NavDirection()
        data class PetProfileToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()

        data class GalleryToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()
    }

    class UnsupportedDirectionException : Exception()
}