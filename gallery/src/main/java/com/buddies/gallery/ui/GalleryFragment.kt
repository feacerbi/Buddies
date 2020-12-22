package com.buddies.gallery.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.view.isVisible
import com.buddies.common.ui.MediaPickerAdapter.MediaSource.CAMERA
import com.buddies.common.ui.MediaPickerAdapter.MediaSource.GALLERY
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.getQuantityString
import com.buddies.common.util.observe
import com.buddies.common.util.openBottomSimpleDialog
import com.buddies.common.util.openMediaPicker
import com.buddies.common.util.registerForNonNullActivityResult
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.common.util.toColorId
import com.buddies.gallery.R
import com.buddies.gallery.databinding.FragmentGalleryBinding
import com.buddies.gallery.util.OpenMultipleDocumentsWithPersistedPermissions
import com.buddies.gallery.viewmodel.GalleryViewModel
import com.buddies.gallery.viewmodel.GalleryViewModel.Action
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.AddGalleryPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.DeleteGalleryPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.RefreshPictures
import com.buddies.gallery.viewmodel.GalleryViewModel.Action.RequestDeletePictures
import com.buddies.gallery.viewstate.GalleryViewEffect.Navigate
import com.buddies.gallery.viewstate.GalleryViewEffect.OpenConfirmDeleteDialog
import com.buddies.gallery.viewstate.GalleryViewEffect.ShowError
import com.buddies.gallery.viewstate.GalleryViewEffect.ShowMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class GalleryFragment : NavigationFragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var viewModel: GalleryViewModel

    private val petIdArg
        get() = arguments?.getString(getString(R.string.pet_id_arg)) ?: ""

    private var newPhotoUri: Uri = Uri.EMPTY

    private val galleryAdapter = GalleryAdapter(this, ::startActionMode)

    private val cameraPick = registerForTrueActivityResult(TakePicture()) {
        perform(AddGalleryPictures(listOf(newPhotoUri)))
    }
    private lateinit var multipleGalleryPick: ActivityResultLauncher<Array<String>>

    private val actionModeCallback: ActionMode.Callback by lazy {
        GalleryActionModeCallback(
            deleteAction = { perform(RequestDeletePictures(galleryAdapter.getSelectedPictureIds())) },
            destroyAction = { galleryAdapter.disableActionMode() }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        multipleGalleryPick = registerForNonNullActivityResult(
            OpenMultipleDocumentsWithPersistedPermissions(context.applicationContext)
        ) {
            perform(AddGalleryPictures(it))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentGalleryBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(petIdArg) }

        setupViews()
        bindViews()
    }

    private fun setupViews() = with (binding) {
        toolbar.setNavigationOnClickListener {
            navigateBack()
        }
        toolbar.inflateMenu(R.menu.gallery_toolbar_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_gallery_menu_action -> {
                    openAddPhotosPicker()
                    true
                }
                R.id.delete_gallery_menu_action -> {
                    startDeleteMode()
                    true
                }
                else -> false
            }
        }
        toolbar.title = getString(R.string.gallery_screen_title)

        picturesGrid.addItemDecoration(GalleryListDecoration())
        picturesGrid.adapter = galleryAdapter

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener { perform(RefreshPictures) }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            galleryAdapter.submitList(it.picturesList)
            emptyPictures.isVisible = it.showEmpty
            refresh.isRefreshing = it.showloading
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is OpenConfirmDeleteDialog -> openConfirmDeleteBottomSheet(it.pictureIds)
                is ShowMessage -> showMessage(it.message)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun openAddPhotosPicker() {
        openMediaPicker {
            when (it) {
                GALLERY -> multipleGalleryPick.launch(arrayOf(IMAGE_MIME_TYPE))
                CAMERA -> cameraPick.launch(newPhotoUri)
            }
        }
    }

    private fun openConfirmDeleteBottomSheet(pictureIds: List<String>) {
        val picturesCount = pictureIds.size

        openBottomSimpleDialog(
            getString(R.string.confirm_delete_title),
            getQuantityString(
                R.plurals.confirm_delete_content,
                picturesCount,
                picturesCount),
            getString(R.string.delete_button),
            {
                galleryAdapter.disableActionMode()
                perform(DeleteGalleryPictures(pictureIds))
            }
        )
    }

    private fun startDeleteMode() {
        galleryAdapter.enableActionMode(requireContext(), startActionMode())
    }

    private fun startActionMode() = binding.toolbar.startActionMode(actionModeCallback)

    private fun showMessage(text: Int, params: Array<String> = arrayOf()) {
        Toast.makeText(requireContext(), getString(text, *params), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }
}