package com.buddies.profile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.observe
import com.buddies.common.util.openBottomEditDialog
import com.buddies.common.util.toColorId
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class ProfileFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentProfileBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener {
            perform(SignOut)
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_picture_menu_action -> {
                    pickGalleryPicture()
                    true
                }
                else -> false
            }
        }

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener {
            perform(Refresh)
        }

        profileNameEdit.setOnClickListener {
            openBottomEditDialog(
                hint = getString(R.string.input_dialog_name_hint),
                text = profileName.text.toString(),
                positiveAction = { perform(ChangeName(it) )}
            )
        }

        myPetsWidget.addOnPetClickListener(this@ProfileFragment) { perform(OpenPetProfile(it.id)) }
        myPetsWidget.addBackPressedHandler(viewLifecycleOwner, requireActivity().onBackPressedDispatcher)
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            profilePicture.load(it.photo.toString()) { createLoadRequest(this@ProfileFragment) }
            profileName.text = it.name
            profileEmail.text = it.email
            myPetsWidget.setExpanded(it.myPetsWidgetExpanded)
            refresh.isRefreshing = it.loading
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun pickGalleryPicture() {
        val pickerIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(pickerIntent,
            GALLERY_IMAGE_PICKER
        )
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == GALLERY_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            intent?.data?.let { perform(ChangePhoto(it)) }
        } else {
            super.onActivityResult(requestCode, resultCode, intent)
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext

    companion object {
        private const val GALLERY_IMAGE_PICKER = 1
    }
}