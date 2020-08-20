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
import com.buddies.profile.util.ProfileTabsMediator
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import com.buddies.profile.viewstate.ProfileViewEffect.*
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

class ProfileFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by sharedViewModel()

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

        pager.adapter = ProfileTabsAdapter(this@ProfileFragment)

        myPetsWidget.addBackPressedHandler(viewLifecycleOwner, requireActivity().onBackPressedDispatcher)
        myPetsWidget.addOnPetClickListener(this@ProfileFragment) {
            perform(OpenPetProfile(it.id))
        }
        myPetsWidget.setExpandedListener {
            perform(SaveExpandedState(it))
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            ProfileTabsMediator(requireContext(), tabs, pager, it.notifications.size).connect()
            profilePicture.load(it.photo.toString()) { createLoadRequest(this@ProfileFragment) }
            myPetsWidget.setExpanded(it.myPetsWidgetExpanded)
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is RefreshPets -> myPetsWidget.refresh()
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