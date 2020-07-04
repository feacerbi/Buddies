package com.buddies.common.usecase

import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseUseCases {

    suspend fun <T> request(
        block: suspend () -> Result<T>
    ): T? = withContext(Dispatchers.IO) {
        when (val result = block.invoke()) {
            is Success -> result.data
            is Fail -> throw DefaultErrorException(result.error)
        }
    }

}