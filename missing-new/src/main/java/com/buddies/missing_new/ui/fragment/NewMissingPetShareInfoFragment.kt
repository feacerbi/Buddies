package com.buddies.missing_new.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.common.util.setOnBackPressed
import com.buddies.contact.ui.adapter.ShareInfoAdapter
import com.buddies.missing_new.R
import com.buddies.missing_new.databinding.FragmentShareInfoBinding
import com.buddies.missing_new.databinding.NewMissingPetHeaderBinding
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.CloseFlow
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Next
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.OnFieldsChanged
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Previous
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class NewMissingPetShareInfoFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentShareInfoBinding
    private lateinit var headerBinding: NewMissingPetHeaderBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()
    private val locationConverter: LocationConverter by inject()

    @SuppressLint("MissingPermission")
    private val requestLocationPermission = registerForTrueActivityResult<String>(ActivityResultContracts.RequestPermission()) {
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
            .addOnSuccessListener { location ->
                shareInfoAdapter.setCurrentLocationField(location)
            }
    }

    private val shareInfoAdapter = ShareInfoAdapter(lifecycleScope, locationConverter, ::requestCurrentLocation) {
            fields -> perform(OnFieldsChanged(fields))
    }

    private fun requestCurrentLocation() {
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentShareInfoBinding.inflate(inflater, container, false).apply {
        binding = this
        headerBinding = NewMissingPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.title = getString(R.string.report_pet_flow_title)
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(Previous) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next(shareInfoAdapter.checkFieldsValid())) }

        shareInfoList.adapter = shareInfoAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            headerBinding.steps.setupIcons(it.stepIcons)
            headerBinding.steps.selectStep(it.step)
            shareInfoAdapter.submitList(it.shareInfoFields)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is NewMissingPetViewEffect.NavigateBack -> navigateBack()
                is Navigate -> navigate(it.direction)
                is NewMissingPetViewEffect.ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}