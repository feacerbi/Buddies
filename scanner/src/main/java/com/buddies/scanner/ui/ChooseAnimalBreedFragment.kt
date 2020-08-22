package com.buddies.scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.scanner.databinding.FragmentChooseAnimalBreedBinding
import com.buddies.scanner.databinding.NewPetHeaderBinding
import com.buddies.scanner.viewmodel.NewPetViewModel
import com.buddies.scanner.viewmodel.NewPetViewModel.Action
import com.buddies.scanner.viewmodel.NewPetViewModel.Action.*
import com.buddies.scanner.viewstate.NewPetViewEffect.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class ChooseAnimalBreedFragment : NavigationFragment() {

    private lateinit var binding: FragmentChooseAnimalBreedBinding
    private lateinit var headerBinding: NewPetHeaderBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    private val animalsAdapter = HorizontalAnimalsAdapter(
        emptyList(),
        this@ChooseAnimalBreedFragment,
        ::handleAnimalPicked)

    private val breedsAdapter = HorizontalBreedsAdapter(
        emptyList(),
        this@ChooseAnimalBreedFragment,
        ::handleBreedPicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentChooseAnimalBreedBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next) }
        animalsList.adapter = animalsAdapter
        breedsList.adapter = breedsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            headerBinding.toolbar.title = getString(it.title)
            headerBinding.steps.selectStep(it.step)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
            animalsAdapter.addItems(it.animalsList)
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is ShowBreeds -> showBreeds(it.breedsList)
                is NavigateBack -> navigateBack()
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showBreeds(breeds: List<Breed>?) {
        breedsAdapter.setItems(breeds)
        binding.breedsList.scheduleLayoutAnimation()
    }

    private fun handleAnimalPicked(animal: Animal) {
        perform(ChooseAnimal(animal))
    }

    private fun handleBreedPicked(breed: Breed) {
        perform(ChooseBreed(breed))
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}