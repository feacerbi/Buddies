package com.buddies.server.api

import com.buddies.server.repository.TagsRepository
import com.buddies.server.util.toTag

class ScannerApi(
    private val tagsRepository: TagsRepository
) : BaseApi() {

    suspend fun getTagByValue(
        tagValue: String
    ) = runWithResult {
        tagsRepository.getTagByValue(tagValue)
            .handleTaskResult()
            .toTag()
    }
}