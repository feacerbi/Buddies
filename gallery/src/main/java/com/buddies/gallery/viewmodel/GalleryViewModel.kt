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
import com.buddies.gallery.R
import com.buddies.gallery.usecase.GalleryUseCases
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.AddGalleryPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.DeleteGalleryPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.RefreshPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.RequestDeletePictures
import com.buddies.gallery.viewstate.GalleryViewEffect
import com.buddies.gallery.viewstate.GalleryViewEffect.OpenConfirmDeleteDialog
import com.buddies.gallery.viewstate.GalleryViewEffect.ShowError
import com.buddies.gallery.viewstate.GalleryViewEffect.ShowMessage
import com.buddies.gallery.viewstate.GalleryViewState
import com.buddies.gallery.viewstate.GalleryViewStateReducer.ShowLoading
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

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        fetchGalleryPictures()
        startUploadPicturesWorkStatusFlow()
        lifecycleRegistry.handleLifecycleEvent(ON_START)
    }

    fun perform(action: Action) {
        when (action) {
            is RefreshPictures -> fetchGalleryPictures()
            is AddGalleryPictures -> addGalleryPictures(action.pictures)
            is RequestDeletePictures -> checkPicturesToDelete(action.pictureIds)
            is DeleteGalleryPictures -> removeGalleryPictures(action.pictureIds)
        }
    }

    private fun fetchGalleryPictures() = safeLaunch(::showError) {
        updateState(ShowLoading)
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
        updateEffect(ShowMessage(R.string.gallery_upload_message))
    }

    private fun checkPicturesToDelete(pictureIds: List<String>) {
        if (pictureIds.isEmpty()) {
            updateEffect(ShowMessage(R.string.delete_pictures_check_message))
        } else {
            updateEffect(OpenConfirmDeleteDialog(pictureIds))
        }
    }

    private fun removeGalleryPictures(list: List<String>) = safeLaunch(::showError) {
        updateState(ShowLoading)
        galleryUseCases.deletePicturesFromGallery(petId, list)
        fetchGalleryPictures()
    }

    private fun showError(error: DefaultError) {
        updateEffect(ShowError(error.code.message))
    }

    override fun onCleared() {
        super.onCleared()
        lifecycleRegistry.handleLifecycleEvent(ON_DESTROY)
    }

    sealed class Action {
        object RefreshPictures : Action()
        data class AddGalleryPictures(val pictures: List<Uri>) : Action()
        data class RequestDeletePictures(val pictureIds: List<String>) : Action()
        data class DeleteGalleryPictures(val pictureIds: List<String>) : Action()
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + dispatcher

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}