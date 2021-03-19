package com.buddies.common.usecase

import com.buddies.common.model.Result
import com.buddies.common.util.handleNonNullResult
import com.buddies.common.util.handleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseUseCases {

    protected suspend fun <T> request(
        block: suspend () -> Result<T>
    ): T? = withContext(Dispatchers.IO) {
        block.invoke().handleResult()
    }

    protected suspend fun <T> requestNonNull(
        block: suspend () -> Result<T>
    ): T = withContext(Dispatchers.IO) {
        block.invoke().handleNonNullResult()
    }

}