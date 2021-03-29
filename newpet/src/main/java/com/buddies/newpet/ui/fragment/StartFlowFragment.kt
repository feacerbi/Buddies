package com.buddies.newpet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.newpet.R
import com.buddies.newpet.databinding.FragmentStartFlowBinding
import com.buddies.newpet.util.FlowType
import com.buddies.newpet.viewmodel.NewPetViewModel
import com.buddies.newpet.viewmodel.NewPetViewModel.Action
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.CloseFlow
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.Next
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.StartFlow
import com.buddies.newpet.viewstate.NewPetViewEffect.Navigate
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StartFlowFragment : NewPetNavigationFragment() {

    private lateinit var binding: FragmentStartFlowBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    private val flowTypeArg
        get() = arguments?.get(getString(R.string.flow_type_arg)) as? FlowType

    private val tagValueArg
        get() = arguments?.getString(getString(R.string.tag_value_arg)) ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStartFlowBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener { perform(CloseFlow) }
        forwardButton.setOnClickListener { perform(Next) }
        perform(StartFlow(flowTypeArg, tagValueArg))
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            startFlowTitle.text = getString(it.startTitle)
            startFlowSubtitle.text = getString(it.startSubtitle)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is Navigate -> navigate(it.direction)
            }
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}