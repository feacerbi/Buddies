package com.buddies.home.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.buddies.common.model.Pet
import com.buddies.common.model.Tag
import com.buddies.common.model.UserInfo
import com.buddies.common.navigation.Navigator.NavDirection.HomeToProfile
import com.buddies.common.ui.bottomsheet.SimpleBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.observe
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.contact.ui.bottomsheet.ShareInfoBottomSheet
import com.buddies.home.R
import com.buddies.home.databinding.FragmentHomeBinding
import com.buddies.home.ui.bottomsheet.ScannedPetBottomSheet
import com.buddies.home.viewmodel.HomeViewModel
import com.buddies.home.viewmodel.HomeViewModel.Action
import com.buddies.home.viewmodel.HomeViewModel.Action.*
import com.buddies.home.viewstate.HomeViewEffect.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class HomeFragment : NavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModel()
    private val locationConverter: LocationConverter by inject()

    private var shareInfoBottomSheet: ShareInfoBottomSheet? = null

    @SuppressLint("MissingPermission")
    val registerLocationPermission = registerForTrueActivityResult(ActivityResultContracts.RequestPermission()) {
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
            .addOnSuccessListener { location -> shareInfoBottomSheet?.setCurrentLocation(location) }
    }

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

        val packageInfo =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)

        version.text = getString(R.string.app_version, packageInfo.versionName)
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            TransitionManager.beginDelayedTransition(root)
            toolbar.isVisible = it.showToolbar
            scanPetButton.isVisible = it.showScanPetButton
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is StopPetScanner -> stopScanner()
                is ShowPetDialog -> showScannedPetBottomSheet(it.pet)
                is ShowAddPetDialog -> showScannedAvailableTagBottomSheet(it.tag)
                is ShowLostPetDialog -> showScannedLostPetBottomSheet(it.pet)
                is ShowShareInfoDialog -> showShareInfoBottomSheet(it.petId, it.user)
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
                perform(OpenPetProfile(pet.id))
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
                perform(NotifyPetFound(pet.id))
            }
            .build()
            .show()
    }

    private fun showScannedAvailableTagBottomSheet(tag: Tag) {
        SimpleBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.pet_tag_found_dialog_title))
            .content(getString(R.string.pet_tag_found_available_dialog_content))
            .cancelButton(getString(R.string.cancel_button))
            .confirmButton(getString(R.string.add_new_pet_button)) {
                perform(AddNewPet(tag))
            }
            .build()
            .show()
    }

    private fun showShareInfoBottomSheet(petId: String, userInfo: UserInfo) {
        shareInfoBottomSheet = ShareInfoBottomSheet.Builder(layoutInflater)
            .name(userInfo.name)
            .email(userInfo.email)
            .phone(checked = false)
            .location(
                checked = false,
                coroutineScope = lifecycleScope,
                locationConverter = locationConverter,
                currentLocationRequest = {
                    registerLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            )
            .cancelButton()
            .confirmButton { perform(SendUserInfo(petId, it)) }
            .build()
            .apply {
                show()
            }
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