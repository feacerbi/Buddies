package com.buddies.profile.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.buddies.profile.ui.fragment.ProfileFavoritesFragment
import com.buddies.profile.ui.fragment.ProfileFragment
import com.buddies.profile.ui.fragment.ProfileInfoFragment
import com.buddies.profile.ui.fragment.ProfileNotificationsFragment
import com.buddies.profile.util.ProfileTabsMediator.TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.FAVORITES_TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.INFO_TAB
import com.buddies.profile.util.ProfileTabsMediator.TAB.NOTIFICATIONS_TAB
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ProfileTabsAdapter(
    fragment: ProfileFragment
) : FragmentStateAdapter(fragment) {

    var showFavoritesTab = false
    set(value) {
        val changed = field != value
        field = value
        if (changed) notifyDataSetChanged()
    }

    var showNotificationsTab = false
        set(value) {
            val changed = field != value
            field = value
            if (changed) notifyDataSetChanged()
        }

    override fun getItemCount(): Int = when {
        !showFavoritesTab && showNotificationsTab -> TAB.values().size - 1
        showFavoritesTab && !showNotificationsTab -> TAB.values().size - 1
        !showFavoritesTab && !showNotificationsTab -> TAB.values().size - 2
        else -> TAB.values().size
    }

    override fun createFragment(position: Int): Fragment = when {
        position == INFO_TAB.position -> ProfileInfoFragment()
        position == FAVORITES_TAB.position && showFavoritesTab -> ProfileFavoritesFragment()
        position == FAVORITES_TAB.position && !showFavoritesTab && showNotificationsTab -> ProfileNotificationsFragment()
        position == NOTIFICATIONS_TAB.position && showNotificationsTab -> ProfileNotificationsFragment()
        else -> throw IllegalArgumentException("illegal position $position, out of bounds (max: $itemCount).")
    }
}