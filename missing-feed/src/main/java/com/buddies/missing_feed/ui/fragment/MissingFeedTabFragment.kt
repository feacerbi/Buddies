package com.buddies.missing_feed.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingType
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.missing_feed.R
import com.buddies.missing_feed.databinding.FragmentMissingFeedTabBinding
import com.buddies.missing_feed.ui.adapter.MissingPetsAdapter
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenMoreFoundPets
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenMoreLostPets
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenPetProfile
import com.buddies.missing_feed.viewstate.MissingFeedViewState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MissingFeedTabFragment : NavigationFragment() {

    private lateinit var binding: FragmentMissingFeedTabBinding

    private val viewModel: MissingFeedViewModel by sharedViewModel()

    private val missingTypeArg
        get() = arguments?.getString(MISSING_TYPE_ARG) ?: ""

    private val missingType by lazy {
        MissingType.fromName(missingTypeArg)
    }

    private val recentPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)
    private val nearPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)
    private val yourPetsAdapter = MissingPetsAdapter(this, ::onPetClicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMissingFeedTabBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        val moreAction = when (missingType) {
            LOST -> OpenMoreLostPets
            FOUND -> OpenMoreFoundPets
        }

        recentsListTitle.text = getString(R.string.recents_list_title, missingType.name.lowercase())
        nearYouListTitle.text = getString(R.string.near_you_list_title, missingType.name.lowercase())
        yourPetsListTitle.text = getString(R.string.your_pets_list_title, missingType.name.lowercase())

        moreRecentsButton.setOnClickListener { perform(moreAction) }
        moreNearYouButton.setOnClickListener { perform(moreAction) }
        moreYourPetsButton.setOnClickListener { perform(moreAction) }

        recentsList.adapter = recentPetsAdapter
        nearYouList.adapter = nearPetsAdapter
        yourPetsList.adapter = yourPetsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            when (missingType) {
                LOST -> bindLostState(it)
                FOUND -> bindFoundState(it)
            }
            recentsList.scheduleLayoutAnimation()
            nearYouList.scheduleLayoutAnimation()
            yourPetsList.scheduleLayoutAnimation()
        }
    }

    private fun bindLostState(state: MissingFeedViewState) = with (binding) {
        recentPetsAdapter.submitList(state.recentLostPets)
        nearPetsAdapter.submitList(state.nearLostPets)
        yourPetsAdapter.submitList(state.yourLostPets)
        recentsGroup.isVisible = state.showLostRecents
        nearYouGroup.isVisible = state.showLostNearYou
        youFoundGroup.isVisible = state.showLostYour
        emptyList.isVisible = state.showLostEmptyList
    }

    private fun bindFoundState(state: MissingFeedViewState) = with (binding) {
        recentPetsAdapter.submitList(state.recentFoundPets)
        nearPetsAdapter.submitList(state.nearFoundPets)
        yourPetsAdapter.submitList(state.yourFoundPets)
        recentsGroup.isVisible = state.showFoundRecents
        nearYouGroup.isVisible = state.showFoundNearYou
        youFoundGroup.isVisible = state.showFoundYour
        emptyList.isVisible = state.showFoundEmptyList
    }

    private fun onPetClicked(pet: MissingPet) {
        perform(OpenPetProfile(pet.id))
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    companion object {
        fun newInstance(missingType: String) = MissingFeedTabFragment().apply {
            arguments = Bundle().apply { putString(MISSING_TYPE_ARG, missingType) }
        }

        private const val MISSING_TYPE_ARG = "missingType"
    }
}