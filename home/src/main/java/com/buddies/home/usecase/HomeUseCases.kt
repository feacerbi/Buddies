package com.buddies.home.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.contact.model.ShareInfo
import com.buddies.server.api.HomeApi

class HomeUseCases(
    private val homeApi: HomeApi
) : BaseUseCases() {

    suspend fun getUser() = request {
        homeApi.getCurrentUserInfo()
    }

    suspend fun getPet(
        tagId: String
    ) = request {
        homeApi.getPet(tagId)
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