package com.buddies

import com.buddies.common.di.commonModule
import com.buddies.configuration.di.configurationModule
import com.buddies.gallery.di.galleryModule
import com.buddies.home.di.homeModule
import com.buddies.login.di.loginModule
import com.buddies.missing_all.di.allMissingModule
import com.buddies.missing_feed.di.missingFeedModule
import com.buddies.missing_new.di.newMissingPetModule
import com.buddies.missing_profile.di.missingPetModule
import com.buddies.mypets.di.myPetsModule
import com.buddies.navigation.di.navigationModule
import com.buddies.newpet.di.newPetModule
import com.buddies.pet.di.petModule
import com.buddies.profile.di.profileModule
import com.buddies.scanner.di.scannerModule
import com.buddies.security.di.securityModule
import com.buddies.server.di.serverModule
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
val baseModules = arrayOf(
    allMissingModule,
    commonModule,
    configurationModule,
    galleryModule,
    homeModule,
    loginModule,
    missingFeedModule,
    missingPetModule,
    myPetsModule,
    navigationModule,
    newMissingPetModule,
    newPetModule,
    petModule,
    profileModule,
    scannerModule,
    securityModule,
    serverModule
)