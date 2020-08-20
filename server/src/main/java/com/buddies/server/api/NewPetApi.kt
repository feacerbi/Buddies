package com.buddies.server.api

import com.buddies.server.repository.TagsRepository
import com.buddies.server.util.toTag

class NewPetApi(
    private val tagsRepository: TagsRepository
) : BaseApi() {

    suspend fun isTagValid(
        tagValue: String
    ) = runWithResult {
        tagsRepository.getTag(tagValue)
            .handleTaskResult()
            .toTag()
            .info.available
    }

}