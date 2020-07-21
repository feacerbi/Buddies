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
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.buddies.common.model.Owner
import com.buddies.common.ui.NavigationFragment
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.observe
import com.buddies.common.util.openEditDialog
import com.buddies.mypets.R
import com.buddies.mypets.databinding.FragmentPetProfileBinding
import com.buddies.mypets.viewmodel.PetProfileViewModel
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action
import com.buddies.mypets.viewmodel.PetProfileViewModel.Action.*
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

    private val ownershipsBottomSheet: OwnershipsBottomDialog by lazy {
        OwnershipsBottomDialog(parentFragmentManager)
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
                else -> false
            }
        }

        profileNameEdit.setOnClickListener {
            openEditDialog(
                hint = getString(R.string.input_dialog_pet_name_hint),
                text = profileName.text.toString(),
                positiveAction = { perform(ChangeName(it) )}
            )
        }

        profileAnimalEdit.setOnClickListener {
            // TODO
//            openEditDialog(
//                hint = getString(R.string.input_dialog_pet_name_hint),
//                text = profileName.text.toString(),
//                positiveAction = { perform(ChangeAnimal(it.toAnimal()) )}
//            )
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
            toolbar.inflateMenu(it.toolbarMenu)
            ownersList.adapter = OwnersAdapter(
                it.owners,
                it.ownershipInfo,
                this@PetProfileFragment,
                { owner -> perform(OpenOwnerProfile(owner)) },
                { owner -> showEditOwnershipBottomSheet(owner) }
            )
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
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

    private fun showEditOwnershipBottomSheet(owner: Owner) {
        ownershipsBottomSheet.show(owner.category) {
            perform(ChangeOwnership(owner, it))
        }
    }

    private fun showMessage(text: Int) {
        Toast.makeText(requireContext(), getString(text), Toast.LENGTH_SHORT).show()
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