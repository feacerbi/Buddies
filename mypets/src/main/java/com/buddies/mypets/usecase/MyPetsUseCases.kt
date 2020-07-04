package com.buddies.mypets.usecase

import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.PetInfo
import com.buddies.common.model.User
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.MyPetsApi

class MyPetsUseCases(
    private val myPetsApi: MyPetsApi
) : BaseUseCases() {

    suspend fun getPetsFromCurrentUser() = request {
        myPetsApi.getPetsFromCurrentUser()
    }

    suspend fun getPetsFromUser(user: User) = request {
        myPetsApi.getPetsFromUser(user.id)
    }

    suspend fun addNewPet(
        petInfo: PetInfo,
        category: OwnershipCategory
    ) = request {
        myPetsApi.addNewPet(petInfo, category)
    }
}