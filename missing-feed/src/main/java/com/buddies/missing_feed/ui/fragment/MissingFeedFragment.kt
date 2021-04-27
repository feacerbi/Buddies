package com.buddies.missing_feed.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isVisible
import com.buddies.common.model.MissingPet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.common.util.toColorId
import com.buddies.missing_feed.R
import com.buddies.missing_feed.databinding.FragmentMissingFeedBinding
import com.buddies.missing_feed.ui.adapter.MissingPetsAdapter
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenMorePets
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenPetProfile
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RegisterNewLocation
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RequestFeedPets
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.ShowMessage
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel

class MissingFeedFragment : NavigationFragment() {

    private lateinit var binding: FragmentMissingFeedBinding

    private val viewModel: MissingFeedViewModel by viewModel()

    @SuppressLint("MissingPermission")
    private val requestLocationPermission = registerForTrueActivityResult(RequestPermission()) {
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
            .addOnSuccessListener { location -> perform(RegisterNewLocation(location)) }
    }

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

    override fun onResume() {
        super.onResume()
        perform(RequestFeedPets)
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

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener { perform(RequestFeedPets) }

        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            title.text = getString(it.title, it.titleName)
            refresh.isRefreshing = it.overallProgress
            recentPetsAdapter.submitList(it.recentPets)
            nearPetsAdapter.submitList(it.nearPets)
            yourPetsAdapter.submitList(it.yourPets)
            nearYouGroup.isVisible = it.showNearYou
            youFoundGroup.isVisible = it.showYour
            emptyList.isVisible = it.showEmptyList
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
        perform(OpenPetProfile(pet.id))
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}