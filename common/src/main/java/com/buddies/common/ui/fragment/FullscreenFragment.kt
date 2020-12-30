package com.buddies.common.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import com.buddies.common.R
import com.buddies.common.databinding.FragmentFullscreenBinding
import com.buddies.common.util.load

class FullscreenFragment : NavigationFragment() {

    private lateinit var binding: FragmentFullscreenBinding

    private val pictureUrl
        get() = arguments?.getString(getString(R.string.picture_url_arg)) ?: ""
    private val transitionName
        get() = arguments?.getString(getString(R.string.transition_name_arg)) ?: ""
    private val transitionBack
        get() = arguments?.getBoolean(getString(R.string.transition_back_arg)) ?: true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFullscreenBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() = with (binding) {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        if (transitionBack.not()) sharedElementReturnTransition = null

        toolbar.setNavigationOnClickListener {
            navigateBack()
        }

        picture.load(pictureUrl, this@FullscreenFragment)
        picture.transitionName = transitionName
    }
}