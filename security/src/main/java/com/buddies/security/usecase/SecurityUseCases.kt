package com.buddies.security.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.SecurityApi

class SecurityUseCases(
    private val securityApi: SecurityApi
) : BaseUseCases() {

    suspend fun fetchEncryptionKey() = request {
        securityApi.getEncryptionKey()
    }

}