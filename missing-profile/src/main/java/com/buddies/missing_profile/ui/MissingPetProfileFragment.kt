package com.buddies.missing_profile.ui

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.buddies.common.model.Animal
import com.buddies.common.model.Breed
import com.buddies.common.model.InfoType
import com.buddies.common.navigation.Navigator.NavDirection.MissingPetToFullscreen
import com.buddies.common.ui.adapter.AnimalsAdapter
import com.buddies.common.ui.adapter.BreedsAdapter
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.CAMERA
import com.buddies.common.ui.adapter.MediaPickerAdapter.MediaSource.GALLERY
import com.buddies.common.ui.bottomsheet.InputBottomSheet
import com.buddies.common.ui.bottomsheet.MediaPickerBottomSheet
import com.buddies.common.ui.bottomsheet.SelectableBottomSheet
import com.buddies.common.ui.bottomsheet.SimpleBottomSheet
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.common.util.*
import com.buddies.contact.model.ContactInfo
import com.buddies.contact.ui.bottomsheet.ContactInfoBottomSheet
import com.buddies.contact.ui.bottomsheet.ShareInfoBottomSheet
import com.buddies.missing_profile.R
import com.buddies.missing_profile.databinding.FragmentMissingPetProfileBinding
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeAnimal
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeName
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangePhoto
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ChangeSharedInfo
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ConfirmPetRemoval
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.ConfirmPetReturned
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.OpenGallery
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.PetReturned
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.Refresh
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RemovePet
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestAnimals
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestBreeds
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel.Action.RequestContactInfo
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.Navigate
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.NavigateBack
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowAnimalsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowBreedsList
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowConfirmRemovalBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowConfirmReturnedBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowContactInfoBottomSheet
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowError
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowMessage
import com.buddies.missing_profile.viewstate.MissingPetViewEffect.ShowShareInfoBottomSheet
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext

class MissingPetProfileFragment : NavigationFragment(), CoroutineScope {

    private lateinit var binding: FragmentMissingPetProfileBinding
    private val viewModel: MissingPetProfileViewModel by viewModel { parametersOf(petIdArg) }

    private val petIdArg
        get() = arguments?.getString(getString(R.string.pet_id_arg)) ?: ""

    private val cameraHelper = CameraHelper(this)
    private val galleryPick = registerForNonNullActivityResult(GetContent()) {
        perform(ChangePhoto(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMissingPetProfileBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                R.id.open_gallery_menu_action -> {
                    perform(OpenGallery)
                    true
                }
                R.id.remove_pet_menu_action -> {
                    perform(RemovePet)
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

        profileReporterEdit.setOnClickListener {
            perform(RequestContactInfo)
        }

        returnedButton.setOnClickListener {
            perform(PetReturned)
        }
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.viewState) {
            petPicture.load(it.photo, this@MissingPetProfileFragment)
            petPicture.setOnClickListener { _ -> navigateToFullscreen(petPicture, it.photo) }
            profileName.text = it.name
            profileAnimal.text = getString(R.string.animal_field, it.animal, it.breed)
            profileNameEdit.isVisible = it.nameEdit
            profileAnimalEdit.isVisible = it.animalEdit
            profileReporterName.text = it.reporter
            profileReporterEdit.isVisible = it.contactInfo
            profileReporterEdit.setImageResource(it.contactInfoIcon)
            returnedButton.isVisible = it.returnedButton
            toolbar.menu.clear()
            toolbar.inflateMenu(it.toolbarMenu)
            refresh.isRefreshing = it.loading
        }

        observe(viewModel.viewEffect) {
            when (it) {
                is ShowAnimalsList -> openAnimalsList(it.list)
                is ShowBreedsList -> openBreedsList(it.list, it.animal)
                is ShowMessage -> showMessage(it.message, it.params.toTypedArray())
                is ShowContactInfoBottomSheet -> openContactInfoBottomSheet(it.info)
                is ShowShareInfoBottomSheet -> openEditShareInfoBottomSheet(it.info)
                is ShowConfirmRemovalBottomSheet -> openConfirmPetRemovalBottomSheet(it.message, it.petName)
                is ShowConfirmReturnedBottomSheet -> openConfirmPetReturnedBottomSheet(it.message, it.petName)
                is Navigate -> navigate(it.direction)
                is NavigateBack -> navigateBack()
                is ShowError -> showMessage(it.error)
            }
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

    private fun openConfirmPetRemovalBottomSheet(@StringRes message: Int, petName: String) {
        SimpleBottomSheet.Builder(layoutInflater)
            .title(getString(message, petName))
            .cancelButton()
            .confirmButton(getString(R.string.remove_button)) {
                perform(ConfirmPetRemoval)
            }
            .build()
            .show()
    }

    private fun openConfirmPetReturnedBottomSheet(@StringRes message: Int, petName: String) {
        SimpleBottomSheet.Builder(layoutInflater)
            .title(getString(message, petName))
            .cancelButton()
            .confirmButton(getString(R.string.confirm_button)) {
                perform(ConfirmPetReturned)
            }
            .build()
            .show()
    }

    private fun openEditShareInfoBottomSheet(info: List<ContactInfo>?) {
        val emailInfo = info?.firstOrNull { it.infoType == InfoType.EMAIL }
        val phoneInfo = info?.firstOrNull { it.infoType == InfoType.PHONE }
        val locationInfo = info?.firstOrNull { it.infoType == InfoType.LOCATION }

        ShareInfoBottomSheet.Builder(layoutInflater)
            .email(email = emailInfo?.info ?: "", checked = emailInfo != null)
            .phone(phone = phoneInfo?.info ?: "", checked = phoneInfo != null)
            .location(location = locationInfo?.info ?: "", checked = locationInfo != null)
            .cancelButton()
            .confirmButton {
                perform(ChangeSharedInfo(it))
            }
            .build()
            .show()
    }

    private fun openContactInfoBottomSheet(info: List<ContactInfo>?) {
        val contactInfoBuilder = ContactInfoBottomSheet.Builder(layoutInflater)

        info?.forEach {
            contactInfoBuilder.field(it)
        }

        contactInfoBuilder
            .closeButton()
            .build()
            .show()
    }

    private fun navigateToFullscreen(image: View, uri: Uri) {
        navigate(
            MissingPetToFullscreen(uri.toString(), image.transitionName),
            image to image.transitionName
        )
    }

    private fun showMessage(text: Int, params: Array<String> = emptyArray()) {
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