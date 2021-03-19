package com.buddies.profile.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.buddies.profile.util.ProfileTabsMediator
import com.buddies.profile.util.ProfileTabsMediator.TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.FAVORITES_TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.INFO_TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.NOTIFICATIONS_TAB
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ProfileTabsAdapter(
    fragment: ProfileFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TAB.values().size

    override fun createFragment(position: Int): Fragment =
        when (position) {
            INFO_TAB.position -> ProfileInfoFragment()
            FAVORITES_TAB.position -> ProfileFavoritesFragment()
            NOTIFICATIONS_TAB.position -> ProfileNotificationsFragment()
            else -> throw IllegalArgumentException("illegal position $position, out of bounds (max: $itemCount).")
        }
}