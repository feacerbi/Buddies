package com.buddies.scanner.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.ScannerApi

class ScannerUseCases(
    private val scannerApi: ScannerApi
) : BaseUseCases() {

    suspend fun getTag(
        tagValue: String
    ) = request {
        scannerApi.getTagByValue(tagValue)
    }
}