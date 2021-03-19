package com.buddies.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.HorizontalSeparatorListDecoration
import com.buddies.common.util.observe
import com.buddies.common.util.toColorId
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileFavoritesTabBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.AddFavorite
import com.buddies.profile.viewmodel.ProfileViewModel.Action.OpenPetProfile
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RefreshFavorites
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RemoveFavorite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class ProfileFavoritesFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileFavoritesTabBinding

    private val viewModel: ProfileViewModel by sharedViewModel()

    private val favoritesAdapter = FavoritesAdapter(this,
        checkFavoriteAction = { favorite -> perform(AddFavorite(favorite)) },
        uncheckFavoriteAction = { favorite -> perform(RemoveFavorite(favorite)) },
        openPetProfileAction = { pet -> perform(OpenPetProfile(pet.id)) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileFavoritesTabBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener {
            perform(RefreshFavorites)
        }

        favoritesAdapter.addLoadStateListener {
            favoritesListEmpty.isVisible = favoritesAdapter.itemCount == 0
        }

        list.addItemDecoration(HorizontalSeparatorListDecoration(requireContext()))
        list.adapter = favoritesAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            favoritesAdapter.submitData(lifecycle, it.favorites)
            refresh.isRefreshing = it.loadingFavorites
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext
}