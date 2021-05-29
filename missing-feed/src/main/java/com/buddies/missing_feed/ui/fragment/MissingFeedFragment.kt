package com.buddies.missing_feed.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.dpToPx
import com.buddies.common.util.observe
import com.buddies.common.util.registerForTrueActivityResult
import com.buddies.common.util.toColorId
import com.buddies.missing_feed.R
import com.buddies.missing_feed.databinding.FragmentMissingFeedBinding
import com.buddies.missing_feed.ui.adapter.MissingFeedTabsAdapter
import com.buddies.missing_feed.util.MissingFeedTabsMediator
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenProfile
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.OpenSettings
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RegisterNewLocation
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.ReportPet
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel.Action.RequestFeed
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.Navigate
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.ShowError
import com.buddies.missing_feed.viewstate.MissingFeedViewEffect.ShowMessage
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class MissingFeedFragment : NavigationFragment() {

    private lateinit var binding: FragmentMissingFeedBinding

    private val viewModel: MissingFeedViewModel by sharedViewModel()

    @SuppressLint("MissingPermission")
    private val requestLocationPermission = registerForTrueActivityResult(RequestPermission()) {
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
            .addOnSuccessListener { location -> perform(RegisterNewLocation(location)) }
    }

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
        perform(RequestFeed)
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setUpViews() = with (binding) {
        settingsButton.setOnClickListener { perform(OpenSettings) }
        profileButton.setOnClickListener { perform(OpenProfile) }
        reportButton.setOnClickListener { perform(ReportPet) }

        val startOffset = SWIPE_REFRESH_OFFSET.dpToPx(requireContext())
        refresh.setProgressViewOffset(true, startOffset, startOffset + refresh.progressViewEndOffset)
        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener { perform(RequestFeed) }

        val windowHeight = requireActivity().windowManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.currentWindowMetrics.bounds.height()
            } else {
                @Suppress("DEPRECATION")
                it.defaultDisplay.height
            }
        }
        pager.minimumHeight = windowHeight
        pager.adapter = MissingFeedTabsAdapter(this@MissingFeedFragment)

        MissingFeedTabsMediator(requireContext(), tabs, pager).connect()
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            title.text = getString(it.title, it.titleName)
            refresh.isRefreshing = it.overallProgress
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowMessage -> showMessage(it.message)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    companion object {
        private const val SWIPE_REFRESH_OFFSET = 105
    }
}