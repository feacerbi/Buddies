package com.buddies.common.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.request.LoadRequestBuilder
import com.buddies.common.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, Observer { action.invoke(it) })
}

var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
fun View.hide() { visibility = View.GONE }
fun View.invisible(apply: Boolean = true) { visibility = if (apply) View.INVISIBLE else View.VISIBLE }
fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun RecyclerView.animate(run: Boolean) {
    if (run) {
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_bottom_up)
        scheduleLayoutAnimation()
    }
}

fun Float?.orZero(): Float = this ?: 0f

fun CoroutineScope.safeLaunch(
    error: (Exception) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) = launch {
    try {
        block.invoke(this)
    } catch (e: Exception) {
        error.invoke(e)
    }
}

fun LoadRequestBuilder.createLoadRequest(
    lifecycleOwner: LifecycleOwner,
    @DrawableRes error: Int = -1
) {
    crossfade(true)
    lifecycle(lifecycleOwner)
    error(error)
}