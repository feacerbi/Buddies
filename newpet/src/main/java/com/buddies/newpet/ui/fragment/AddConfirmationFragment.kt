package com.buddies.newpet.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.buddies.common.util.invisible
import com.buddies.common.util.load
import com.buddies.common.util.observe
import com.buddies.newpet.R
import com.buddies.newpet.databinding.FragmentAddPetConfirmationBinding
import com.buddies.newpet.viewmodel.NewPetViewModel
import com.buddies.newpet.viewmodel.NewPetViewModel.Action
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.CloseFlow
import com.buddies.newpet.viewstate.NewPetViewEffect.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddConfirmationFragment : NewPetNavigationFragment() {

    private lateinit var binding: FragmentAddPetConfirmationBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddPetConfirmationBinding.inflate(layoutInflater, container, false).apply {
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

    @SuppressLint("StringFormatInvalid")
    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            TransitionManager.beginDelayedTransition(root)
            confirmationTitle.text = getString(it.confirmationTitle, it.animalName)
            progress.isVisible = it.confirmationLoading
            animalPhoto.invisible(it.hideAnimalPhoto)
            animalPhoto.load(it.animalPhoto, this@AddConfirmationFragment) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets
            }
            backButton.isVisible = it.showBackButton
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
}