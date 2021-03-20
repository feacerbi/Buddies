package com.buddies.mypets.usecase

import com.buddies.common.model.User
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.PetApi

class MyPetsUseCases(
    private val petApi: PetApi
) : BaseUseCases() {

    suspend fun getBuddiesFromCurrentUser() = request {
        petApi.getBuddiesFromCurrentUser()
    }

    suspend fun getBuddiesFromUser(user: User) = request {
        petApi.getBuddiesFromUser(user.id)
    }

}