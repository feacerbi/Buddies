package com.buddies.mypets.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import coil.api.load
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.Owner
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.*
import com.buddies.mypets.R
import com.buddies.mypets.databinding.FragmentPetProfileBinding
import com.buddies.mypets.databinding.OwnerInviteListBinding
import com.buddies.mypets.model.OwnersComparator
import com.buddies.mypets.viewmodel.PetProfileViewModel
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action.*
import com.buddies.mypets.viewstate.PetProfileViewEffect.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class PetProfileFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentPetProfileBinding
    private lateinit var viewModel: PetProfileViewModel

    private val petIdArg
        get() = arguments?.getString(getString(R.string.pet_id_arg)) ?: ""

    private val ownershipsBottomSheet: OwnershipsBottomDialog by lazy {
        OwnershipsBottomDialog(parentFragmentManager)
    }

    private val ownersPagingAdapter by lazy {
        OwnersPagingAdapter(OwnersComparator, this@PetProfileFragment)
    }

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
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_picture_menu_action -> {
                    pickGalleryPicture()
                    true
                }
                R.id.add_owner_menu_action -> {
                    showInviteOwnerBottomSheet()
                    true
                }
                else -> false
            }
        }

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener {
            perform(Refresh)
        }

        profileNameEdit.setOnClickListener {
            openBottomEditDialog(
                hint = getString(R.string.input_dialog_pet_name_hint),
                text = profileName.text.toString(),
                positiveAction = { perform(ChangeName(it) )}
            )
        }

        profileAnimalEdit.setOnClickListener {
            perform(RequestAnimals)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            petPicture.load(it.photo.toString()) { createLoadRequest(this@PetProfileFragment) }
            profileName.text = it.name
            profileAnimal.text = getString(R.string.animal_field, it.animal, it.breed)
            profileTagNumber.text = it.tag
            profileNameEdit.isVisible = it.nameEdit
            profileAnimalEdit.isVisible = it.animalEdit
            profileTagEdit.isVisible = it.tagEdit
            toolbar.menu.clear()
            toolbar.inflateMenu(it.toolbarMenu)
            ownersList.adapter = OwnersAdapter(
                it.owners,
                it.ownershipInfo,
                this@PetProfileFragment,
                onClick = { owner -> perform(OpenOwnerProfile(owner)) },
                onOwnershipClick = { owner -> showEditOwnershipBottomSheet(owner) }
            )
            ownersPagingAdapter.submitData(lifecycle, it.pagingData)
            refresh.isRefreshing = it.loading
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is ShowAnimalsList -> openAnimalsList(it.list)
                is ShowBreedsList -> openBreedsList(it.list, it.animal)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun pickGalleryPicture() {
        val pickerIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(pickerIntent,
            GALLERY_IMAGE_PICKER
        )
    }

    private fun showInviteOwnerBottomSheet() {
        val customView = OwnerInviteListBinding.inflate(layoutInflater)
        val dialog = openCustomBottomSheet(customView.root)

        ownersPagingAdapter.addDataRefreshListener {
            customView.ownersListEmpty.isVisible = ownersPagingAdapter.itemCount == 0
        }

        customView.searchBox.addTextChangedListener {
            perform(RequestInviteOwners(it.toString()))
        }

        customView.ownersList.adapter = ownersPagingAdapter.apply {
            onClick = { owner ->
                dialog.setOnDismissListener { showInviteOwnershipBottomSheet(owner) }
                dialog.cancel()
            }
        }
    }

    private fun showInviteOwnershipBottomSheet(owner: Owner) {
        ownershipsBottomSheet.show(owner.category, R.string.send_button) {
            perform(InviteOwner(Owner(owner.user, it)))
            // TODO Move message to ViewModel
            showMessage("${getString(it.title)} ownership invitation sent to ${owner.user.info.name}!")
        }
    }

    private fun showEditOwnershipBottomSheet(owner: Owner) {
        ownershipsBottomSheet.show(owner.category) {
            perform(ChangeOwnership(owner, it))
        }
    }

    private fun openAnimalsList(list: List<Animal>?) {
        val animalsAdapter = AnimalsAdapter(list, this)

        openBottomSelectableDialog(
            getString(R.string.select_animal_title),
            animalsAdapter,
            { perform(RequestBreeds(animalsAdapter.getSelected())) },
            getString(R.string.next_button)
        )
    }

    private fun openBreedsList(list: List<Breed>?, animal: Animal) {
        val breedsAdapter = BreedsAdapter(list, this)

        openBottomSelectableDialog(
            getString(R.string.select_breed_title),
            breedsAdapter,
            { perform(ChangeAnimal(animal, breedsAdapter.getSelected())) },
            getString(R.string.change_button)
        )
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == GALLERY_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            intent?.data?.let { perform(ChangePhoto(it)) }
        } else {
            super.onActivityResult(requestCode, resultCode, intent)
        }
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext

    companion object {
        private const val GALLERY_IMAGE_PICKER = 1
    }
}