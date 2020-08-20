package com.buddies.scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.scanner.R
import com.buddies.scanner.databinding.FragmentScanTagBinding
import com.buddies.scanner.viewmodel.NewPetViewModel
import com.buddies.scanner.viewmodel.NewPetViewModel.Action
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.ScanAgain
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.ValidateTag
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class ScanTagFragment : NavigationFragment() {

    private lateinit var binding: FragmentScanTagBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentScanTagBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener { navigateBack() }
        toolbar.title = getString(R.string.new_buddy_flow_title)
        steps.selectStep(1)
        scanAgainButton.setOnClickListener { perform(ScanAgain) }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            qrResult.text = getString(it.result)
            progress.isVisible = it.isLoading
            scanAgainButton.isVisible = it.showScanButton
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is StartCamera -> bindCamera()
                is StopCamera -> stopCamera()
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun bindCamera() {
        observe(binding.scanner.scan(this@ScanTagFragment)) {
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