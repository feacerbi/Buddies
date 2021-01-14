package com.buddies.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.buddies.common.model.Pet
import com.buddies.common.model.UserInfo
import com.buddies.common.navigation.Navigator.NavDirection.HomeToPetProfile
import com.buddies.common.navigation.Navigator.NavDirection.HomeToProfile
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.observe
import com.buddies.home.R
import com.buddies.home.databinding.FragmentHomeBinding
import com.buddies.home.viewmodel.HomeViewModel
import com.buddies.home.viewmodel.HomeViewModel.Action
import com.buddies.home.viewmodel.HomeViewModel.Action.*
import com.buddies.home.viewstate.HomeViewEffect.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class HomeFragment : NavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModel()

    private var cameraHelper = CameraHelper(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener {
            perform(CloseScanner)
        }
        profileButton.setOnClickListener {
            navigate(HomeToProfile)
        }
        scanPetButton.setOnClickListener {
            perform(ScanPet)
            observe(binding.scanner.scan(this@HomeFragment, cameraHelper)) {
                perform(HandleTag(it))
            }
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            TransitionManager.beginDelayedTransition(root)
            toolbar.isVisible = it.showToolbar
            scanPetButton.isVisible = it.showScanPetButton
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is StopPetScanner -> stopScanner()
                is ShowPetDialog -> showScannedPetBottomSheet(it.pet)
                is ShowLostPetDialog -> showScannedLostPetBottomSheet(it.pet)
                is ShowShareInfoDialog -> showShareInfoBottomSheet(it.user)
                is ShowMessage -> showMessage(it.message)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showScannedPetBottomSheet(pet: Pet) {
        ScannedPetBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.pet_tag_found_dialog_title))
            .content(getString(R.string.pet_tag_found_dialog_content))
            .petIcon(pet.info.photo, this)
            .petName(pet.info.name)
            .petStatus(pet.info.lost)
            .cancelButton(getString(R.string.close_button))
            .confirmButton(getString(R.string.open_profile_button)) {
                perform(CloseScanner)
                navigate(HomeToPetProfile(pet.id))
            }
            .build()
            .show()
    }

    private fun showScannedLostPetBottomSheet(pet: Pet) {
        ScannedPetBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.pet_tag_found_dialog_title))
            .content(getString(R.string.pet_tag_found_lost_dialog_content))
            .petIcon(pet.info.photo, this)
            .petName(pet.info.name)
            .petStatus(pet.info.lost)
            .cancelButton(getString(R.string.cancel_button))
            .confirmButton(getString(R.string.notify_button)) {
                perform(NotifyPetFound)
            }
            .build()
            .show()
    }

    private fun showShareInfoBottomSheet(userInfo: UserInfo) {
        ShareInfoBottomSheet.Builder(layoutInflater)
            .name(userInfo.name)
            .email(userInfo.email)
            .phone(checked = false)
            .location(checked = false)
            .cancelButton()
            .confirmButton { perform(SendUserInfo(it)) }
            .build()
            .show()
    }

    private fun stopScanner() {
        binding.scanner.stopScan()
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}