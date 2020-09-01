package com.buddies.scanner.ui

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import coil.api.load
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.invisible
import com.buddies.common.util.observe
import com.buddies.scanner.databinding.FragmentAddPetConfirmationBinding
import com.buddies.scanner.viewmodel.NewPetViewModel
import com.buddies.scanner.viewmodel.NewPetViewModel.Action
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.CloseFlow
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddConfirmationFragment : NavigationFragment() {

    private lateinit var binding: FragmentAddPetConfirmationBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentAddPetConfirmationBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                perform(CloseFlow)
            }
        })

        backButton.setOnClickListener {
            navigateBack()
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            TransitionManager.beginDelayedTransition(root)
            confirmationTitle.text = getString(it.confirmationTitle, it.animalName)
            progress.isVisible = it.confirmationLoading
            animalPhoto.invisible(it.hideAnimalPhoto)
            animalPhoto.load(it.animalPhoto) { createLoadRequest(this@AddConfirmationFragment, true) }
            backButton.isVisible = it.showBackButton
        }

        observe(viewModel.getEffectStream()) {
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
}