package com.buddies.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.lifecycleScope
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.CAMERA
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.GALLERY
import com.buddies.common.ui.bottomsheet.MediaPickerBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.load
import com.buddies.common.util.observe
import com.buddies.common.util.registerForNonNullActivityResult
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileBinding
import com.buddies.profile.model.ContactInfo
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

    private var tabsMediator: ProfileTabsMediator? = null
    private var expandedWidget = false

    private val cameraHelper = CameraHelper(this)
    private val galleryPick = registerForNonNullActivityResult(GetContent()) {
        perform(ChangePhoto(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileBinding.inflate(layoutInflater, container, false).apply {
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
                    openEditPhotoPicker()
                    true
                }
                else -> false
            }
        }

        pager.adapter = ProfileTabsAdapter(this@ProfileFragment)

        tabsMediator = ProfileTabsMediator(requireContext(), tabs, pager, 0)
            .apply { connect() }

        myPetsWidget.apply {
            setup(this@ProfileFragment)
            addBackPressedHandler(viewLifecycleOwner, requireActivity().onBackPressedDispatcher)
            addOnPetClickListener { perform(OpenPetProfile(it.id)) }
            addNewPetClickListener { perform(OpenNewPetFlow) }
            setExpanded(expandedWidget)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            tabsMediator?.updateBadge(it.notifications.size)
            profilePicture.load(it.photo, this@ProfileFragment)
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is RefreshPets -> myPetsWidget.refresh()
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
                is ShowContactInfoBottomSheet -> openContactInfoBottomSheet(it.info)
            }
        }
    }

    private fun openContactInfoBottomSheet(info: List<ContactInfo>) {
        val contactInfoBuilder = ContactInfoBottomSheet.Builder(layoutInflater)

        info.forEach {
            contactInfoBuilder.field(it)
        }

        contactInfoBuilder
            .closeButton()
            .build()
            .show()
    }

    private fun openEditPhotoPicker() {
        MediaPickerBottomSheet.Builder(layoutInflater)
            .selected {
                when (it) {
                    GALLERY -> galleryPick.launch(IMAGE_MIME_TYPE)
                    CAMERA -> cameraHelper.launchCamera(requireContext()) { uri ->
                        perform(ChangePhoto(uri))
                    }
                }
            }
            .build()
            .show()
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override fun onStop() {
        super.onStop()
        expandedWidget = binding.myPetsWidget.isExpanded()
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }
}