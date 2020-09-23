package com.buddies.common.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.metadata
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

class ImageCache(
    private val imageLoader: ImageLoader
) : LifecycleObserver {

    private val list = LinkedHashMap<MemoryCache.Key, String>()

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
        image: String?,
        options: RequestOptions.() -> Unit
    ) {
        val key = imageView.metadata?.memoryCacheKey

        when {
            key == null -> imageLoader.enqueue(
                createRequest(
                    lifecycleOwner,
                    imageView,
                    image,
                    RequestOptions().apply(options) )
            )
            shouldLoadImage(key, image) -> imageLoader.enqueue(
                createRequest(
                    lifecycleOwner,
                    imageView,
                    imageLoader.memoryCache[key],
                    RequestOptions().apply(options))
            )
            else -> { /* Skip */ }
        }
    }

    private fun createRequest(
        lifecycleOwner: LifecycleOwner,
        imageView: ImageView,
        image: String?,
        options: RequestOptions
    ) = ImageRequest.Builder(imageView.context).apply {
        data(image)
        target(imageView)
        listener { _, metadata ->
            cacheImage(lifecycleOwner, metadata.memoryCacheKey, image)
        }
        lifecycle(lifecycleOwner)
        applyOptions(lifecycleOwner, options)
    }.build()

    private fun createRequest(
        lifecycleOwner: LifecycleOwner,
        imageView: ImageView,
        image: Bitmap?,
        options: RequestOptions
    ) = ImageRequest.Builder(imageView.context).apply {
        data(image)
        target(imageView)
        lifecycle(lifecycleOwner)
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

    private fun shouldLoadImage(
        key: MemoryCache.Key,
        image: String?
    ) = list.containsKey(key).not() || list.containsKey(key) && list[key] != image

    private fun cacheImage(
        lifecycleOwner: LifecycleOwner,
        key: MemoryCache.Key?,
        image: String?
    ) {
        if (key != null && image != null) {
            list[key] = image
            addClearCacheObserver(lifecycleOwner, key)
        }
    }

    private fun addClearCacheObserver(
        lifecycleOwner: LifecycleOwner,
        key: MemoryCache.Key
    ) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearCache() {
                list.remove(key)
            }
        })
    }
}