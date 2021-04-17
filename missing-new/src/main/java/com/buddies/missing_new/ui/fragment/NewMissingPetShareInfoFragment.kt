package com.buddies.missing_new.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.util.expand
import com.buddies.common.util.observe
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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewMissingPetShareInfoFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentShareInfoBinding
    private lateinit var headerBinding: NewMissingPetHeaderBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()

    private val shareInfoAdapter = ShareInfoAdapter { fields, validated ->
        perform(OnFieldsChanged(fields, validated))
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
        forwardButton.setOnClickListener { perform(Next) }

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