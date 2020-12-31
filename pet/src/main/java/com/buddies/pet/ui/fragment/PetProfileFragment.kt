package com.buddies.pet.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.Owner
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToFullscreen
import com.buddies.common.navigation.Navigator.NavDirection.PetProfileToGallery
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.CAMERA
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.GALLERY
import com.buddies.common.ui.bottomsheet.CustomBottomSheet
import com.buddies.common.ui.bottomsheet.InputBottomSheet
import com.buddies.common.ui.bottomsheet.MediaPickerBottomSheet
import com.buddies.common.ui.bottomsheet.SelectableBottomSheet
import com.buddies.common.ui.bottomsheet.SimpleBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.*
import com.buddies.pet.R
import com.buddies.pet.databinding.FragmentPetProfileBinding
import com.buddies.pet.databinding.OwnerInviteListBinding
import com.buddies.pet.ui.adapter.AnimalsAdapter
import com.buddies.pet.ui.adapter.BreedsAdapter
import com.buddies.pet.ui.adapter.OwnersAdapter
import com.buddies.pet.ui.adapter.OwnersPagingAdapter
import com.buddies.pet.ui.view.OwnershipsBottomDialog
import com.buddies.pet.viewmodel.PetProfileViewModel
import com.buddies.pet.viewmodel.PetProfileViewModel.Action
import com.buddies.pet.viewmodel.PetProfileViewModel.Action.*
import com.buddies.pet.viewstate.PetProfileViewEffect.*
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
        OwnersPagingAdapter(this@PetProfileFragment)
    }

    private val cameraHelper = CameraHelper(this)
    private val galleryPick = registerForNonNullActivityResult(GetContent()) {
        perform(ChangePhoto(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPetProfileBinding.inflate(layoutInflater, container, false).apply {
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
                    openEditPhotoPicker()
                    true
                }
                R.id.add_owner_menu_action -> {
                    showInviteOwnerBottomSheet()
                    true
                }
                R.id.open_gallery_menu_action -> {
                    navigate(PetProfileToGallery(petIdArg))
                    true
                }
                else -> false
            }
        }

        refresh.setColorSchemeResources(R.attr.colorSecondary.toColorId(requireContext()))
        refresh.setOnRefreshListener { perform(Refresh) }

        profileNameEdit.setOnClickListener {
            InputBottomSheet.Builder(layoutInflater)
                .hint(getString(R.string.input_dialog_pet_name_hint))
                .content(profileName.text.toString())
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .cancelButton()
                .confirmButton(getString(R.string.change_button)) { perform(ChangeName(it)) }
                .build()
                .show()
        }

        profileAnimalEdit.setOnClickListener {
            perform(RequestAnimals)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            petPicture.load(it.photo, this@PetProfileFragment)
            petPicture.setOnClickListener { _ -> navigateToFullscreen(petPicture, it.photo) }
            profileName.text = it.name
            profileAnimal.text = getString(R.string.animal_field, it.animal, it.breed)
            profileTagNumber.text = it.tag
            profileNameEdit.isVisible = it.nameEdit
            profileAnimalEdit.isVisible = it.animalEdit
            profileTagEdit.isVisible = it.tagEdit
            profileReportLostSwitch.isVisible = it.lostSwitch
            profileReportLostSwitch.isChecked = it.lost
            profileReportLostSwitch.setOnCheckedChangeListener { _, checked ->
                perform(ReportLost(it.name, it.lost, checked))
            }
            profileReportLostStatus.text = getString(it.lostStatus)
            toolbar.menu.clear()
            toolbar.inflateMenu(it.toolbarMenu)
            // TODO Set items with diff
            ownersList.adapter = OwnersAdapter(
                this@PetProfileFragment,
                it.owners,
                it.ownershipInfo,
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
                is ShowBottomMessage -> showMessage(it.message, it.params)
                is ShowConfirmDialog -> showConfirmDialog(it.message, it.name)
                is Navigate -> navigate(it.direction)
                is ShowError -> showMessage(it.error)
            }
        }
    }

    private fun showInviteOwnerBottomSheet() {
        val customView = OwnerInviteListBinding.inflate(layoutInflater)
        val dialog = CustomBottomSheet.Builder(customView.root).build()

        ownersPagingAdapter.addLoadStateListener {
            customView.ownersListEmpty.isVisible = ownersPagingAdapter.itemCount == 0
        }

        customView.searchBox.addTextChangedListener {
            perform(RequestInviteOwners(it.toString()))
        }

        customView.ownersList.adapter = ownersPagingAdapter.apply {
            onClick = { owner ->
                dialog.cancel { showInviteOwnershipBottomSheet(owner) }
            }
        }

        dialog.show()
    }

    private fun showInviteOwnershipBottomSheet(owner: Owner) {
        ownershipsBottomSheet.show(owner.category, R.string.send_button) {
            perform(InviteOwner(Owner(owner.user, it)))
        }
    }

    private fun showEditOwnershipBottomSheet(owner: Owner) {
        ownershipsBottomSheet.show(owner.category, R.string.change_button) {
            perform(ChangeOwnership(owner, it))
        }
    }

    private fun openAnimalsList(list: List<Animal>?) {
        val animalsAdapter = AnimalsAdapter(this, list)

        SelectableBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.select_animal_title))
            .adapter(animalsAdapter)
            .cancelButton()
            .confirmButton(getString(R.string.next_button)) {
                perform(RequestBreeds(animalsAdapter.getSelected()))
            }
            .build()
            .show()
    }

    private fun openBreedsList(list: List<Breed>?, animal: Animal) {
        val breedsAdapter = BreedsAdapter(this, list)

        SelectableBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.select_breed_title))
            .adapter(breedsAdapter)
            .cancelButton()
            .confirmButton(getString(R.string.change_button)) {
                perform(ChangeAnimal(animal, breedsAdapter.getSelected()))
            }
            .build()
            .show()
    }

    private fun openEditPhotoPicker() {
        MediaPickerBottomSheet.Builder(layoutInflater)
            .selected {
                when (it) {
                    GALLERY -> galleryPick.launch(IMAGE_MIME_TYPE)
                    CAMERA -> cameraHelper.launchCamera(requireContext()) { uri ->
                        perform(ChangePhoto(uri))
                    }
                }
            }
            .build()
            .show()
    }

    private fun navigateToFullscreen(image: View, uri: Uri) {
        navigate(
            PetProfileToFullscreen(uri.toString(), image.transitionName),
            image to image.transitionName
        )
    }

    private fun showConfirmDialog(message: Int, name: String) {
        SimpleBottomSheet.Builder(layoutInflater)
            .title(getString(R.string.report_lost_dialog_title))
            .content(getString(message, name))
            .cancelButton {
                perform(CancelLost)
            }
            .confirmButton(getString(R.string.report_lost_dialog_confirm_button)) {
                perform(ConfirmLost(name))
            }
            .build()
            .show()
    }

    private fun showMessage(text: Int, params: Array<String> = arrayOf()) {
        Toast.makeText(requireContext(), getString(text, *params), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }
}