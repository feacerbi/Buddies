package com.buddies.common.util

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

class ImageHandler(
    private val imageLoader: ImageLoader
) : LifecycleObserver {

    fun load(
        lifecycleOwner: LifecycleOwner,
        imageView: ImageView,
        uri: Uri?,
        options: RequestOptions.() -> Unit
    ) {
        load(lifecycleOwner, imageView, uri?.toString(), options)
    }

    fun load(
        lifecycleOwner: LifecycleOwner,
        imageView: ImageView,
        new: String?,
        options: RequestOptions.() -> Unit
    ) {
        imageLoader.enqueue(
            createRequest(
                lifecycleOwner,
                imageView,
                new,
                RequestOptions().apply(options))
        )
    }

    private fun createRequest(
        lifecycleOwner: LifecycleOwner,
        imageView: ImageView,
        new: String?,
        options: RequestOptions
    ) = ImageRequest.Builder(imageView.context).apply {
        data(new)
        target(imageView)
        applyOptions(lifecycleOwner, options)
    }.build()

    private fun ImageRequest.Builder.applyOptions(
        lifecycleOwner: LifecycleOwner? = null,
        options: RequestOptions = RequestOptions()
    ) {
        crossfade(options.crossfade)
        lifecycle(lifecycleOwner)
        if (options.error != -1) error(options.error)
        if (options.circleTransform) transformations(CircleCropTransformation())
    }

    companion object {
        const val MEMORY_POOL_PERCENTAGE = 0.5
    }
}