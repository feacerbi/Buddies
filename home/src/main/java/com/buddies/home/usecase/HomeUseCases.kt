package com.buddies.home.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.home.model.ShareInfo
import com.buddies.server.api.HomeApi

class HomeUseCases(
    private val homeApi: HomeApi
) : BaseUseCases() {

    suspend fun getUser() = request {
        homeApi.getCurrentUserInfo()
    }

    suspend fun getPet(
        tagValue: String
    ) = request {
        homeApi.getPetByTag(tagValue)
    }

    suspend fun notifyPetFound(
        petId: String,
        shareInfoList: List<ShareInfo>
    ) = request {
        homeApi.notifyPetFound(
            petId,
            shareInfoList.map { it.toPair() }.toMap())
    }
}