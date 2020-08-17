package com.buddies.server.api

import com.buddies.server.repository.SecurityRepository
import com.buddies.server.util.toEncryptionKey

class SecurityApi(
    private val securityRepository: SecurityRepository
) : BaseApi() {

    suspend fun getEncryptionKey() = runWithResult {
        securityRepository.getEncryptionKey()
            .handleTaskResult()
            .toEncryptionKey()
    }

}