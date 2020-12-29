package com.buddies.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.buddies.common.model.UserInfo
import com.buddies.common.navigation.Navigator.NavDirection.HomeToProfile
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.customTextAppearance
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { perform(CloseScanner) }
            })

        toolbar.setNavigationOnClickListener {
            perform(CloseScanner)
        }
        profileButton.setOnClickListener {
            navigate(HomeToProfile)
        }
        scanPetButton.setOnClickListener {
            perform(ScanPet)
        }
        scannerMask.scanAgainButton.setOnClickListener {
            perform(ScanPet)
        }
        notifyButton.setOnClickListener {
            perform(NotifyPetFound)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            toolbar.isVisible = it.showScanner
            scanner.isVisible = it.showScanner
            scannerMask.maskRoot.isVisible = it.showScanner
            scannerMask.progress.isVisible = it.showLoading
            scannerMask.scanAgainButton.isVisible = it.showScanAgainButton
            scannerMask.qrResult.text = getString(it.result, it.scannedName)
                .customTextAppearance(requireContext(), arrayOf(it.scannedName), R.style.BoldResult)
            scanPetButton.isVisible = it.showScanPetButton
            notifyButton.isVisible = it.showNotifyButton
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is StartCamera -> bindCamera()
                is StopCamera -> stopCamera()
                is ShowShareInfoDialog -> showShareInfoBottomSheet(it.user)
                is ShowMessage -> showMessage(it.message)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
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

    private fun bindCamera() {
        observe(binding.scanner.scan(this@HomeFragment, cameraHelper)) {
            perform(ValidateTag(it))
        }
    }

    private fun stopCamera() {
        cameraHelper.stopCamera(requireContext())
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}