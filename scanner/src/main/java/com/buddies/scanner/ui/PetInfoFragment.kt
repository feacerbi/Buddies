package com.buddies.scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import coil.api.load
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.scanner.databinding.FragmentPetInfoBinding
import com.buddies.scanner.databinding.NewPetHeaderBinding
import com.buddies.scanner.viewmodel.NewPetViewModel
import com.buddies.scanner.viewmodel.NewPetViewModel.Action
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.*
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PetInfoFragment : NewPetNavigationFragment() {

    private lateinit var binding: FragmentPetInfoBinding
    private lateinit var headerBinding: NewPetHeaderBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    private val galleryPick = registerForActivityResult(ActivityResultContracts.GetContent()) {
        perform(PhotoInput(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPetInfoBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next) }

        nameInputEditText.addTextChangedListener {
            sendInput()
        }
        ageInputEditText.addTextChangedListener {
            sendInput()
        }

        animalPhoto.setOnClickListener {
            galleryPick.launch(IMAGE_MIME_TYPE)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            headerBinding.toolbar.title = getString(it.title)
            headerBinding.steps.selectStep(it.step)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
            animalPhoto.load(it.animalPhoto) { createLoadRequest(this@PetInfoFragment, true) }
            cameraOverlay.isVisible = it.showCameraOverlay
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is NavigateBack -> navigateBack()
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun sendInput() = with (binding) {
        perform(InfoInput(
            nameInputEditText.text.toString(),
            ageInputEditText.text.toString()))
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }
}