package com.buddies.missing_new.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.util.expand
import com.buddies.common.util.observe
import com.buddies.common.util.setOnBackPressed
import com.buddies.missing_new.R
import com.buddies.missing_new.databinding.FragmentNewMissingPetChooseAnimalBreedBinding
import com.buddies.missing_new.databinding.NewMissingPetHeaderBinding
import com.buddies.missing_new.ui.adapter.HorizontalAnimalsAdapter
import com.buddies.missing_new.ui.adapter.HorizontalBreedsAdapter
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseAnimal
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.ChooseBreed
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.CloseFlow
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Next
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel.Action.Previous
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.Navigate
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.NavigateBack
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowBreeds
import com.buddies.missing_new.viewstate.NewMissingPetViewEffect.ShowError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewMissingPetChooseAnimalBreedFragment : NewMissingPetNavigationFragment() {

    private lateinit var binding: FragmentNewMissingPetChooseAnimalBreedBinding
    private lateinit var headerBinding: NewMissingPetHeaderBinding

    private val viewModel: NewMissingPetViewModel by sharedViewModel()

    private val animalsAdapter = HorizontalAnimalsAdapter(
        this@NewMissingPetChooseAnimalBreedFragment,
        emptyList(),
        ::handleAnimalPicked)

    private val breedsAdapter = HorizontalBreedsAdapter(
        this@NewMissingPetChooseAnimalBreedFragment,
        emptyList(),
        ::handleBreedPicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentNewMissingPetChooseAnimalBreedBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        headerBinding = NewMissingPetHeaderBinding.bind(this.root)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        headerBinding.toolbar.title = getString(R.string.report_pet_flow_title)
        headerBinding.toolbar.setNavigationOnClickListener { perform(CloseFlow) }

        setOnBackPressed { perform(Previous) }
        backButton.setOnClickListener { perform(Previous) }
        forwardButton.setOnClickListener { perform(Next(validated = true)) }

        animalsList.adapter = animalsAdapter
        breedsList.adapter = breedsAdapter
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            headerBinding.steps.setupIcons(it.stepIcons)
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