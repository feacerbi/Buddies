package com.buddies.server.api

import com.buddies.common.model.DefaultError
import com.buddies.common.model.ErrorCode.*
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.toDefaultError
import com.buddies.common.util.toException
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseApi : CoroutineScope {

    private val db = Firebase.firestore

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + Dispatchers.IO

    protected suspend fun <T> runWithResult(
        block: suspend () -> T
    ): Result<T> =
        try {
            Success(block.invoke())
        } catch (exception: Exception) {
            Fail(exception.toDefaultError())
        }

    protected suspend fun <T> Task<T>?.handleTaskResult(
    ) = suspendCoroutine<T> { cont ->
        if (this == null) {
            cont.resumeWithException(DefaultError(TASK_NULL).toException())
        } else {
            addOnCompleteListener {
                val result = it.result

                when {
                    isSuccessful && result != null -> cont.resume(result)
                    isSuccessful && result == null -> cont.resumeWithException(DefaultError(RESULT_NULL).toException())
                    isSuccessful.not() -> cont.resumeWithException(DefaultError(TASK_FAIL).toException())
                }
            }
        }
    }

    protected suspend fun runTransactionsWithResult(
        vararg functions: Transaction.() -> Unit
    ) = runWithResult {
        db.runTransaction { transaction ->
            functions.forEach { it.invoke(transaction) }
        }.handleTaskResult()
    }
}