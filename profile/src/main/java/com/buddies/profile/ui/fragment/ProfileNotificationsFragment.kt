package com.buddies.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.observe
import com.buddies.common.util.toColorId
import com.buddies.common.util.HorizontalSeparatorListDecoration
import com.buddies.notification.ui.NotificationsAdapter
import com.buddies.profile.R
import com.buddies.profile.databinding.FragmentProfileNotificationsTabBinding
import com.buddies.profile.viewmodel.ProfileViewModel
import com.buddies.profile.viewmodel.ProfileViewModel.Action
import com.buddies.profile.viewmodel.ProfileViewModel.Action.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class ProfileNotificationsFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentProfileNotificationsTabBinding

    private val viewModel: ProfileViewModel by sharedViewModel()

    private val notificationsAdapter by lazy {
        NotificationsAdapter(
            owner = this@ProfileNotificationsFragment,
            acceptAction = { notification -> perform(AcceptNotification(notification)) },
            infoAction = { notification -> perform(NotificationInfoClick(notification)) },
            dismissAction = { notification -> perform(IgnoreNotification(notification)) },
            iconClickAction = { notification -> perform(NotificationIconClick(notification)) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileNotificationsTabBinding.inflate(layoutInflater, container, false).apply {
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

        list.addItemDecoration(HorizontalSeparatorListDecoration(requireContext()))
        list.adapter = notificationsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            notificationsListEmpty.isVisible = it.emptyNotifications
            notificationsAdapter.submitList(it.notifications)
            refresh.isRefreshing = it.loadingNotifications
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext
}