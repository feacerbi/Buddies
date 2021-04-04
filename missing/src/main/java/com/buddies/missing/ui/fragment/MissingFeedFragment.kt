package com.buddies.missing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.model.MissingPet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.missing.databinding.FragmentMissingFeedBinding
import com.buddies.missing.ui.adapter.MissingPetsAdapter
import com.buddies.missing.viewmodel.MissingFeedViewModel
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenMorePets
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenPetProfileFromFeed
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing.viewstate.MissingFeedViewEffect.ShowMessage
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MissingFeedFragment : NavigationFragment() {

    private lateinit var binding: FragmentMissingFeedBinding

    private val viewModel: MissingFeedViewModel by sharedViewModel()

    private val recentPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)
    private val nearPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)
    private val yourPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMissingFeedBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        profileButton.setOnClickListener { perform(OpenProfile) }
        reportButton.setOnClickListener { perform(ReportPet) }

        moreRecentsButton.setOnClickListener { perform(OpenMorePets) }
        moreNearYouButton.setOnClickListener { perform(OpenMorePets) }
        moreYouFoundButton.setOnClickListener { perform(OpenMorePets) }

        recentsList.adapter = recentPetsAdapter
        nearYouList.adapter = nearPetsAdapter
        youFoundList.adapter = yourPetsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            title.text = getString(it.title, it.titleName)
            recentPetsAdapter.submitList(it.recentPets)
            nearPetsAdapter.submitList(it.nearPets)
            yourPetsAdapter.submitList(it.yourPets)
            recentsList.scheduleLayoutAnimation()
            nearYouList.scheduleLayoutAnimation()
            youFoundList.scheduleLayoutAnimation()
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowMessage -> showMessage(it.message)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun onPetClicked(pet: MissingPet) {
        perform(OpenPetProfileFromFeed(pet.id))
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}