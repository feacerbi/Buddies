package com.buddies.missing_new.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.buddies.common.util.expand
import com.buddies.common.util.load
import com.buddies.common.util.observe
import com.buddies.common.util.registerForNonNullActivityResult
import com.buddies.common.util.setOnBackPressed
import com.buddies.missing_new.databinding.FragmentNewMissingPetInfoBinding
import com.buddies.missing_new.databinding.NewMissingPetHeaderBinding
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.*
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.NavigateBack
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewMissingPetInfoFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentNewMissingPetInfoBinding
    private lateinit var headerBinding: NewMissingPetHeaderBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()

    private val galleryPick = registerForNonNullActivityResult(ActivityResultContracts.GetContent()) {
        perform(PhotoInput(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentNewMissingPetInfoBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewMissingPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(Previous) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next(validated = true)) }

        nameInputEditText.addTextChangedListener {
            perform(InfoInput(
                nameInputEditText.text.toString(),
                descriptionInputEditText.text.toString())
            )
        }

        descriptionInputEditText.addTextChangedListener {
            perform(InfoInput(
                nameInputEditText.text.toString(),
                descriptionInputEditText.text.toString())
            )
        }

        animalPhoto.setOnClickListener { galleryPick.launch(IMAGE_MIME_TYPE) }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            headerBinding.toolbar.title = getString(it.flowTitle)
            headerBinding.steps.setupIcons(it.stepIcons)
            headerBinding.steps.selectStep(it.step)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
            nameInputLayout.isVisible = it.showName
            animalPhoto.load(it.animalPhoto, this@NewMissingPetInfoFragment) {
                circleTransform = true
            }
            cameraOverlay.isVisible = it.showCameraOverlay
            backButton.isVisible = it.showBack
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is NavigateBack -> navigateBack()
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
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