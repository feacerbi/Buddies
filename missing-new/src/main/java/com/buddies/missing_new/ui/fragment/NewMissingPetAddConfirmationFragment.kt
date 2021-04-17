package com.buddies.missing_new.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.buddies.common.util.invisible
import com.buddies.common.util.load
import com.buddies.common.util.observe
import com.buddies.common.util.setOnBackPressed
import com.buddies.missing_new.R
import com.buddies.missing_new.databinding.FragmentAddNewMissingPetConfirmationBinding
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.CloseFlow
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.NavigateBack
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewMissingPetAddConfirmationFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentAddNewMissingPetConfirmationBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddNewMissingPetConfirmationBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(CloseFlow) }
    }

    @SuppressLint("StringFormatInvalid")
    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            TransitionManager.beginDelayedTransition(root)
            confirmationTitle.text = getString(it.confirmationTitle, it.animalName)
            progress.isVisible = it.confirmationLoading
            animalPhoto.invisible(it.hideAnimalPhoto)
            animalPhoto.load(it.animalPhoto, this@NewMissingPetAddConfirmationFragment) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets
            }
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