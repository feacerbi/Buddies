package com.buddies.missing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.missing.R
import com.buddies.missing.databinding.FragmentAllMissingPetsBinding
import com.buddies.missing.viewmodel.MissingFeedViewModel
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.RequestAllPets
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AllMissingPetsFragment : NavigationFragment() {

    private lateinit var binding: FragmentAllMissingPetsBinding

    private val viewModel: MissingFeedViewModel by sharedViewModel()

    private val petsAdapter = MissingPetsPagingAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAllMissingPetsBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.title = getString(R.string.all_missing_pets_title)
        toolbar.setNavigationOnClickListener { navigateBack() }

        petsList.adapter = petsAdapter

        perform(RequestAllPets)
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            petsAdapter.submitData(lifecycle, it.allPets)
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}