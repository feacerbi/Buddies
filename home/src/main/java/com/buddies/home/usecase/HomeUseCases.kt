package com.buddies.home.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.HomeApi

class HomeUseCases(
    private val homeApi: HomeApi
) : BaseUseCases() {

    suspend fun getPet(
        tagValue: String
    ) = request {
        homeApi.getPetByTag(tagValue)
    }

    suspend fun notifyPetFound(
        petId: String
    ) = request {
        homeApi.notifyPetFound(petId)
    }
}