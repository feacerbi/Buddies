package com.buddies.common.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import coil.request.LoadRequestBuilder
import coil.transform.CircleCropTransformation
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.UNKNOWN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, Observer { action.invoke(it) })
}

var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun LoadRequestBuilder.createLoadRequest(
    lifecycleOwner: LifecycleOwner? = null,
    circleTransform: Boolean = false,
    @DrawableRes error: Int = -1
) {
    crossfade(true)
    lifecycle(lifecycleOwner)
    if (error != -1) error(error)
    if (circleTransform) transformations(CircleCropTransformation())
}

fun CoroutineScope.safeLaunch(
    error: (DefaultError) -> Unit = {},
    context: CoroutineContext = coroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = launch(context) {
    try {
        block.invoke(this)
    } catch (exception: Exception) {
        error.invoke(exception.toDefaultError())
    }
}

fun Exception.toDefaultError() =
    when (this) {
        is DefaultErrorException -> error
        else -> DefaultError(UNKNOWN)
    }

fun DefaultError.toException() =
    DefaultErrorException(this)

fun generateNewId() = UUID.randomUUID().toString()
