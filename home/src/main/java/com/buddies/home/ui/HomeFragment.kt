package com.buddies.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isVisible
import com.buddies.common.navigation.Navigator.NavDirection.HomeToProfile
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.customTextAppearance
import com.buddies.common.util.observe
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.home.R
import com.buddies.home.databinding.FragmentHomeBinding
import com.buddies.home.viewmodel.HomeViewModel
import com.buddies.home.viewmodel.HomeViewModel.Action
import com.buddies.home.viewmodel.HomeViewModel.Action.*
import com.buddies.home.viewstate.HomeViewEffect.*
import com.buddies.scanner.ui.QRScanner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class HomeFragment : NavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModel()

    private val permissionRequest = registerForTrueActivityResult(RequestPermission()) {
        qrScanner?.permissionResultSuccess()
    }

    private var qrScanner: QRScanner? = null

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
        }
        scannerMask.scanAgainButton.setOnClickListener {
            perform(ScanPet)
        }
        notifyButton.setOnClickListener {
            perform(NotifyPetFound)
        }
        qrScanner = scanner.apply {
            setupPermissionRequest(permissionRequest)
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
                is ShowMessage -> showMessage(it.message)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun bindCamera() {
        observe(binding.scanner.scan(this@HomeFragment)) {
            perform(ValidateTag(it))
        }
    }

    private fun stopCamera() {
        binding.scanner.stop(requireContext())
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}