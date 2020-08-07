package com.buddies.profile.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.buddies.profile.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileTabsMediator(
    context: Context,
    private val tabLayout: TabLayout,
    private val viewPager2: ViewPager2,
    private val badgeNumber: Int = 0
) {

    private val strategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
        tab.text = when (position) {
            0 -> context.resources.getString(R.string.info_tab_title)
            1 -> context.resources.getString(R.string.notifications_tab_title)
            else -> throw IllegalStateException("illegal position $position, out of bounds (max: 2).")
        }
        tab.icon = when (position) {
            0 -> context.resources.getDrawable(R.drawable.ic_baseline_person, context.theme)
            1 -> context.resources.getDrawable(R.drawable.ic_baseline_notifications, context.theme)
            else -> throw IllegalStateException("illegal position $position, out of bounds (max: 2).")
        }
        tab.removeBadge()
        if (position == 1 && badgeNumber > 0) tab.orCreateBadge.apply {
            number = badgeNumber
            badgeTextColor = ContextCompat.getColor(context, android.R.color.white)
        }
    }

    fun connect() = TabLayoutMediator(tabLayout, viewPager2, strategy).attach()
}