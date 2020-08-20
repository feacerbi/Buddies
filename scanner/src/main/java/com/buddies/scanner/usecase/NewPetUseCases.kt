package com.buddies.scanner.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.NewPetApi

class NewPetUseCases(
    private val newPetApi: NewPetApi
) : BaseUseCases() {

    suspend fun validateTag(
        tagValue: String
    ) = request {
        newPetApi.isTagValid(tagValue)
    }

}