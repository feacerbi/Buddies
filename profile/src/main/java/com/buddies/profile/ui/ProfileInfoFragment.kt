package com.buddies.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.common.util.openBottomEditDialog
import com.buddies.common.util.toColorId
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileInfoTabBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.ChangeName
import com.buddies.profile.viewmodel.ProfileViewModel.Action.RefreshInfo
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

class ProfileInfoFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileInfoTabBinding

    private val viewModel: ProfileViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentProfileInfoTabBinding.inflate(layoutInflater, container, false).apply {
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
            perform(RefreshInfo)
        }

        profileNameEdit.setOnClickListener {
            openBottomEditDialog(
                hint = getString(R.string.input_dialog_name_hint),
                text = profileName.text.toString(),
                positiveAction = { perform(ChangeName(it) )}
            )
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            profileName.text = it.name
            profileEmail.text = it.email
            refresh.isRefreshing = it.loadingInfo
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext
}