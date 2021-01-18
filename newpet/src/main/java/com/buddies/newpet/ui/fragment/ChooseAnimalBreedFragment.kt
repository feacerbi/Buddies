package com.buddies.newpet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.newpet.R
import com.buddies.newpet.databinding.FragmentChooseAnimalBreedBinding
import com.buddies.newpet.databinding.NewPetHeaderBinding
import com.buddies.newpet.ui.adapter.HorizontalAnimalsAdapter
import com.buddies.newpet.ui.adapter.HorizontalBreedsAdapter
import com.buddies.newpet.viewmodel.NewPetViewModel
import com.buddies.newpet.viewmodel.NewPetViewModel.Action
import com.buddies.newpet.viewmodel.NewPetViewModel.Action.*
import com.buddies.newpet.viewstate.NewPetViewEffect.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChooseAnimalBreedFragment : NewPetNavigationFragment() {

    private lateinit var binding: FragmentChooseAnimalBreedBinding
    private lateinit var headerBinding: NewPetHeaderBinding

    private val viewModel: NewPetViewModel by sharedViewModel()

    private val animalsAdapter = HorizontalAnimalsAdapter(
        this@ChooseAnimalBreedFragment,
        emptyList(),
        ::handleAnimalPicked)

    private val breedsAdapter = HorizontalBreedsAdapter(
        this@ChooseAnimalBreedFragment,
        emptyList(),
        ::handleBreedPicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChooseAnimalBreedBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.title = getString(R.string.new_buddy_flow_title)
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next) }
        animalsList.adapter = animalsAdapter
        breedsList.adapter = breedsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            headerBinding.steps.selectStep(it.step)
            forwardButton.isEnabled = it.forwardButtonEnabled
            forwardButton.expand(it.forwardButtonExpanded)
            forwardButton.text = getString(it.forwardButtonText)
            animalsAdapter.addItems(it.animalsList)
        }

        observe(viewModel.viewEffect) {
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