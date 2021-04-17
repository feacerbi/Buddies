package com.buddies.newpet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.common.util.setOnBackPressed
import com.buddies.newpet.R
import com.buddies.newpet.databinding.FragmentTagScanBinding
import com.buddies.newpet.databinding.NewPetHeaderBinding
import com.buddies.newpet.viewmodel.NewPetViewModel
import com.buddies.newpet.viewmodel.NewPetViewModel.Action
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.*
import com.buddies.newpet.viewstate.NewPetViewEffect.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class NewPetTagScanFragment : NewPetNavigationFragment() {

    private lateinit var binding: FragmentTagScanBinding
    private lateinit var headerBinding: NewPetHeaderBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    private val tagValueArg
        get() = arguments?.getString(getString(R.string.tag_value_arg)) ?: ""

    private val cameraHelper = CameraHelper(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTagScanBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.title = getString(R.string.new_buddy_flow_title)
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(Previous) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next) }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            headerBinding.steps.setupIcons(it.stepIcons)
            headerBinding.steps.selectStep(it.step)
            forwardButton.text = getString(it.forwardButtonText)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            scanner.setResultMessage(getString(it.result))
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }

        observe(binding.scanner.scan(this@NewPetTagScanFragment, cameraHelper, tagValueArg)) {
            perform(HandleTag(it))
        }
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}