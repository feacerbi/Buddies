package com.buddies.gallery.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import com.buddies.common.navigation.Navigator.NavDirection.GalleryToFullscreen
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.CAMERA
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.GALLERY
import com.buddies.common.ui.bottomsheet.MediaPickerBottomSheet
import com.buddies.common.ui.bottomsheet.SimpleBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.getQuantityString
import com.buddies.common.util.observe
import com.buddies.common.util.registerForNonNullActivityResult
import com.buddies.common.util.toColorId
import com.buddies.gallery.R
import com.buddies.gallery.databinding.FragmentGalleryBinding
import com.buddies.gallery.ui.adapter.GalleryAdapter
import com.buddies.gallery.ui.util.GalleryActionModeCallback
import com.buddies.gallery.ui.util.GalleryListDecoration
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
import com.buddies.server.model.Picture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class GalleryFragment : NavigationFragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var viewModel: GalleryViewModel

    private val petIdArg
        get() = arguments?.getString(getString(R.string.pet_id_arg)) ?: ""

    private val editEnabled
        get() = arguments?.getBoolean(getString(R.string.edit_enabled_arg)) ?: false

    private val galleryAdapter = GalleryAdapter(this, ::startActionMode, ::navigateToFullscreen)

    private val cameraHelper = CameraHelper(this)
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

        viewModel = getViewModel { parametersOf(petIdArg, editEnabled) }

        setupViews()
        bindViews()
    }

    private fun setupViews() = with (binding) {
        toolbar.title = getString(R.string.gallery_screen_title)
        toolbar.setNavigationOnClickListener {
            navigateBack()
        }
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

        picturesGrid.addItemDecoration(GalleryListDecoration())
        picturesGrid.adapter = galleryAdapter

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener { perform(RefreshPictures) }

        postponeEnterTransition()
        picturesGrid.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            toolbar.menu.clear()
            toolbar.inflateMenu(it.toolbarMenu)
            galleryAdapter.submitList(it.picturesList)
            emptyPictures.isVisible = it.showEmpty
            refresh.isRefreshing = it.showLoading
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is OpenConfirmDeleteDialog -> openConfirmDeleteBottomSheet(it.pictureIds)
                is ShowMessage -> showMessage(it.message)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun openAddPhotosPicker() {
        MediaPickerBottomSheet.Builder(layoutInflater)
            .selected {
                when (it) {
                    GALLERY -> multipleGalleryPick.launch(arrayOf(IMAGE_MIME_TYPE))
                    CAMERA -> cameraHelper.launchCamera(requireContext()) { uri ->
                        perform(AddGalleryPictures(listOf(uri)))
                    }
                }
            }
            .build()
            .show()
    }

    private fun openConfirmDeleteBottomSheet(pictureIds: List<String>) {
        val picturesCount = pictureIds.size

        SimpleBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.confirm_delete_title))
            .content(getQuantityString(R.plurals.confirm_delete_content, picturesCount, picturesCount))
            .cancelButton()
            .confirmButton(getString(R.string.delete_button)) {
                galleryAdapter.disableActionMode()
                perform(DeleteGalleryPictures(pictureIds))
            }
            .build()
            .show()
    }

    private fun startDeleteMode() {
        galleryAdapter.enableActionMode(requireContext(), startActionMode())
    }

    private fun startActionMode() = binding.toolbar.startActionMode(actionModeCallback)

    private fun navigateToFullscreen(image: View, item: Picture) {
        navigate(
            GalleryToFullscreen(item.downloadUri.toString(), image.transitionName),
            image to image.transitionName
        )
    }

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