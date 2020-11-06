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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseApi {

    private val db = Firebase.firestore

    protected suspend fun <T> runWithResult(
        block: suspend () -> T
    ): Result<T> =
        try {
            Success(block.invoke())
        } catch (exception: Exception) {
            Fail(exception.toDefaultError())
        }

    protected suspend fun <T> Task<T>?.handleTaskResult(
    ): T = suspendCoroutine { cont ->
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

    protected suspend fun runTransactions(
        vararg functions: Transaction.() -> Unit
    ): Unit = db.runTransaction { transaction ->
        functions.forEach { it.invoke(transaction) }
    }.handleTaskResult()

    protected suspend fun runTransactionsWithResult(
        vararg functions: Transaction.() -> Unit
    ) = runWithResult {
        db.runTransaction { transaction ->
            functions.forEach { it.invoke(transaction) }
        }.handleTaskResult()
    }
}