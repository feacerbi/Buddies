package com.buddies.pet.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buddies.common.util.load
import com.buddies.pet.databinding.GalleryItemBinding

class GalleryFragment : Fragment() {

    private lateinit var binding: GalleryItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = GalleryItemBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.picture.load(handleArgument(), this)
    }

    private fun handleArgument() =
        arguments?.takeIf { it.containsKey(URI_EXTRA) }?.let {
            Uri.parse(it.getString(URI_EXTRA))
        }

    companion object {
        const val URI_EXTRA = "uri"
    }
}