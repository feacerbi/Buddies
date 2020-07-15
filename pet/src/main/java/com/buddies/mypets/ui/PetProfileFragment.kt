package com.buddies.mypets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.observe
import com.buddies.mypets.R
import com.buddies.mypets.databinding.FragmentPetProfileBinding
import com.buddies.mypets.viewmodel.PetProfileViewModel
import com.buddies.mypets.viewstate.PetProfileViewEffect.Navigate
import com.buddies.mypets.viewstate.PetProfileViewEffect.ShowError
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext

class PetProfileFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentPetProfileBinding
    private lateinit var viewModel: PetProfileViewModel

    private val petIdArg
        get() = arguments?.getString(getString(R.string.pet_id_arg)) ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPetProfileBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(petIdArg) }

        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        toolbar.setNavigationOnClickListener {
            navigateBack()
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            petPicture.load(it.photo.toString()) { createLoadRequest(this@PetProfileFragment) }
            profileName.text = it.name
            profileAnimal.text = getString(R.string.animal_field, it.animal, it.breed)
            profileTagNumber.text = it.tag
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext
}