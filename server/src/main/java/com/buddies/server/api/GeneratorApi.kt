package com.buddies.server.api

import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.TAG_EXISTS
import com.buddies.common.model.TagInfo
import com.buddies.server.repository.TagsRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GeneratorApi(
    private val tagsRepository: TagsRepository
) : BaseApi() {

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ) = runWithResult {
        val auth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password)
            .handleTaskResult()

        auth.currentUser != null
    }

    suspend fun checkTagValueExists(
        tagValue: String
    ) = runWithResult {
        tagValueExists(tagValue)
    }

    suspend fun addNewPetTag(
        tagValue: String,
        tagEncryptedValue: String
    ) = runWithResult {
        val tagExists = tagValueExists(tagValue)

        if (tagExists) {
            throw DefaultErrorException(DefaultError(TAG_EXISTS))
        }

        val tagInfo = TagInfo(
            tagValue,
            tagEncryptedValue,
            true
        )

        runTransactions(
            tagsRepository.addTag(tagInfo)
        )
    }

    private suspend fun tagValueExists(tagValue: String): Boolean {
        val tags = tagsRepository.getTagByValue(tagValue)
            .handleTaskResult()

        return tags.documents.isNotEmpty()
    }
}