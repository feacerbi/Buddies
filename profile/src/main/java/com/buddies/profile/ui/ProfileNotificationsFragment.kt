package com.buddies.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.common.util.toColorId
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileNotificationsTabBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

class ProfileNotificationsFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileNotificationsTabBinding

    private val viewModel: ProfileViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentProfileNotificationsTabBinding.inflate(layoutInflater, container, false).apply {
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
            perform(RefreshNotifications)
        }

        list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            list.adapter = NotificationsAdapter(
                it.notifications,
                owner = this@ProfileNotificationsFragment,
                ignoreAction = { notification -> perform(IgnoreNotification(notification)) },
                acceptAction = { notification -> perform(AcceptNotification(notification)) }
            )
            notificationsListEmpty.isVisible = it.emptyNotifications
            refresh.isRefreshing = it.loadingNotifications
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext
}