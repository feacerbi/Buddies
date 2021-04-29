package com.buddies.missing_all.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.paging.LoadState
import com.buddies.common.ui.bottomsheet.RadioBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.Sorting
import com.buddies.common.util.hideKeyboard
import com.buddies.common.util.isDisplayed
import com.buddies.common.util.observe
import com.buddies.missing_all.R
import com.buddies.missing_all.databinding.FragmentAllMissingPetsBinding
import com.buddies.missing_all.ui.adapter.MissingPetsPagingAdapter
import com.buddies.missing_all.viewmodel.AllMissingViewModel
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.ChangeSorting
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.OpenPetProfile
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.RequestSorting
import com.buddies.missing_all.viewmodel.AllMissingViewModel.Action.Search
import com.buddies.missing_all.viewstate.AllMissingViewEffect.Navigate
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowError
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowMessage
import com.buddies.missing_all.viewstate.AllMissingViewEffect.ShowSortingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllMissingPetsFragment : NavigationFragment() {

    private lateinit var binding: FragmentAllMissingPetsBinding
    private val viewModel: AllMissingViewModel by viewModel()

    private val petsAdapter = MissingPetsPagingAdapter(this) {
        perform(OpenPetProfile(it.id))
    }

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
        backButton.setOnClickListener { navigateBack() }

        petsAdapter.addLoadStateListener {
            petsListEmpty.isVisible = petsAdapter.itemCount == 0
            progress.isVisible = it.refresh == LoadState.Loading
        }

        petsList.adapter = petsAdapter

        searchBox.setOnEditorActionListener { _, _, _ ->
            searchBox.clearFocus()
            searchBox.hideKeyboard()
            true
        }
        searchBox.doOnTextChanged { text, _, _, _ ->
            perform(Search(text.toString()))
        }

        sortButton.setOnClickListener {
            perform(RequestSorting)
        }

        clearButton.setOnClickListener {
            searchBox.setText("")
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            petsAdapter.submitData(lifecycle, it.allPets)
            sortButton.isDisplayed = it.showSorting
            searchButton.isVisible = it.showSearch
            clearButton.isVisible = it.showClear
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is ShowSortingDialog -> openSortingDialog(it.currentSorting)
                is Navigate -> navigate(it.direction)
                is ShowMessage -> showMessage(it.message)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun openSortingDialog(currentSorting: Sorting) {
        RadioBottomSheet.Builder<Sorting>(layoutInflater)
            .title(getString(R.string.sorting_dialog_title))
            .items(Sorting.values().toList(), { getString(it.title) }, currentSorting)
            .cancelButton()
            .confirmButton(getString(R.string.apply_button)) {
                perform(ChangeSorting(it))
            }
            .build()
            .show()
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}