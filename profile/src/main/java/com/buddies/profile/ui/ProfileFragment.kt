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
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileBinding
import com.buddies.profile.databinding.InputTextLayoutBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect.Navigate
import com.buddies.profile.viewstate.ProfileViewEffect.ShowError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        profileNameEdit.setOnClickListener {
            openEditDialog(
                hint = getString(R.string.input_dialog_name_hint),
                text = profileName.text.toString(),
                positiveAction = { perform(ChangeName(it) )}
            )
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

        setUpBottomSheet()
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            profilePicture.load(it.photo.toString()) { createLoadRequest(this@ProfileFragment) }
            profileName.text = it.name
            profileEmail.text = it.email
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

    private fun openEditDialog(
        hint: String = "",
        text: String = "",
        positiveAction: (String) -> Unit
    ) {
        val inputView = InputTextLayoutBinding.inflate(layoutInflater)

        inputView.inputLayout.hint = hint
        inputView.inputEditText.setText(text)

        MaterialAlertDialogBuilder(requireContext())
            .setView(inputView.root)
            .setNeutralButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.change_button)) { dialog, _ ->
                positiveAction.invoke(inputView.inputEditText.text.toString())
                dialog.dismiss()
            }
            .show()

        inputView.inputEditText.requestFocus()
    }

    private fun setUpBottomSheet() = with (binding) {
        val behavior = BottomSheetBehavior.from(infoBottomSheet)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                profilePicture.alpha = 1 - slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED -> expandIcon.setImageResource(R.drawable.ic_expand_less)
                    else -> expandIcon.setImageResource(R.drawable.ic_expand_more)
                }
            }
        })

        infoBottomSheet.setOnClickListener {
            when (behavior.state) {
                STATE_COLLAPSED -> behavior.state = STATE_HALF_EXPANDED
            }
        }

        expandIcon.setOnClickListener {
            behavior.state = STATE_COLLAPSED
        }

        launch {
            behavior.state = STATE_HALF_EXPANDED
            delay(250)
            behavior.state = STATE_COLLAPSED
        }
    }

    private fun showMessage(text: String?) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
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