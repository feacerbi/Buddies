package com.buddies.missing_new.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.buddies.common.model.MissingType
import com.buddies.common.util.observe
import com.buddies.common.util.setOnBackPressed
import com.buddies.missing_new.R
import com.buddies.missing_new.databinding.FragmentNewMissingPetTypeBinding
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseType
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.CloseFlow
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Next
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Previous
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.NavigateBack
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewMissingPetTypeFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentNewMissingPetTypeBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentNewMissingPetTypeBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next(validated = true)) }

        typeGroup.setOnCheckedChangeListener { _, id ->
            val type = when (id) {
                R.id.lost_radio_button -> MissingType.LOST
                R.id.found_radio_button -> MissingType.FOUND
                else -> MissingType.LOST
            }

            perform(ChooseType(type))
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
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
}