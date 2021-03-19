package com.buddies.profile.util

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.buddies.profile.R
import com.buddies.profile.util.ProfileTabsMediator.TAB.NOTIFICATIONS_TAB
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileTabsMediator(
    context: Context,
    private val tabLayout: TabLayout,
    private val viewPager2: ViewPager2,
    private var badgeNumber: Int = 0
) {

    private val strategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
        //tab.text = context.resources.getString(TAB.byPosition(position).title)
        tab.icon = ResourcesCompat.getDrawable(context.resources, TAB.byPosition(position).icon, context.theme)
        tab.removeBadge()
        if (position == NOTIFICATIONS_TAB.position && badgeNumber > 0) tab.orCreateBadge.apply {
            number = badgeNumber
            badgeTextColor = ContextCompat.getColor(context, android.R.color.white)
        }
    }

    fun updateBadge(number: Int) {
        badgeNumber = number

        tabLayout.getTabAt(NOTIFICATIONS_TAB.position)?.let {
            strategy.onConfigureTab(it, NOTIFICATIONS_TAB.position)
        }
    }

    fun connect() = TabLayoutMediator(tabLayout, viewPager2, strategy).attach()

    internal enum class TAB(
        val position: Int,
        @StringRes val title: Int,
        @DrawableRes val icon: Int
    ) {
        INFO_TAB(0, R.string.info_tab_title, R.drawable.ic_baseline_person),
        FAVORITES_TAB(1, R.string.favorites_tab_title, R.drawable.ic_baseline_favorite),
        NOTIFICATIONS_TAB(2, R.string.notifications_tab_title, R.drawable.ic_baseline_notifications);

        companion object {
            fun byPosition(position: Int) = when (position) {
                INFO_TAB.position -> INFO_TAB
                FAVORITES_TAB.position -> FAVORITES_TAB
                NOTIFICATIONS_TAB.position -> NOTIFICATIONS_TAB
                else -> throw IllegalArgumentException("Illegal position $position, out of bounds (max: 1).")
            }
        }
    }
}