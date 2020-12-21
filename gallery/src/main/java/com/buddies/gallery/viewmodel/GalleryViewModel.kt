package com.buddies.gallery.viewmodel

import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.buddies.common.model.DefaultError
import com.buddies.common.util.safeLaunch
import com.buddies.common.viewmodel.StateViewModel
import com.buddies.gallery.usecase.GalleryUseCases
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.AddGalleryPictures
import com.buddies.gallery.viewstate.GalleryViewEffect
import com.buddies.gallery.viewstate.GalleryViewEffect.ShowError
import com.buddies.gallery.viewstate.GalleryViewState
import com.buddies.gallery.viewstate.GalleryViewStateReducer.ShowPictures
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class GalleryViewModel(
    private val petId: String,
    private val galleryUseCases: GalleryUseCases,
    private val dispatcher: CoroutineDispatcher
) : StateViewModel<GalleryViewState, GalleryViewEffect>(GalleryViewState()), CoroutineScope, LifecycleOwner {

    fun getStateStream() = viewState
    fun getEffectStream() = viewEffect

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        fetchGalleryPictures()
        startUploadPicturesWorkStatusFlow()
        lifecycleRegistry.handleLifecycleEvent(ON_START)
    }

    fun perform(action: Action) {
        when (action) {
            is AddGalleryPictures -> addGalleryPictures(action.pictures)
        }
    }

    private fun fetchGalleryPictures() = safeLaunch(::showError) {
        val galleryPictures = galleryUseCases.getGalleryPictures(petId)
        updateState(ShowPictures(galleryPictures))
    }

    private fun startUploadPicturesWorkStatusFlow() = safeLaunch(::showError) {
        galleryUseCases.getUploadWorkStatus(this@GalleryViewModel).collectLatest {
            if (it == WorkInfo.State.SUCCEEDED) fetchGalleryPictures()
        }
    }

    private fun addGalleryPictures(list: List<Uri>) = safeLaunch(::showError) {
        galleryUseCases.addPicturesToGallery(petId, list)

    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    override fun onCleared() {
        super.onCleared()
        lifecycleRegistry.handleLifecycleEvent(ON_DESTROY)
    }

    sealed class Action {
        data class AddGalleryPictures(val pictures: List<Uri>) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}