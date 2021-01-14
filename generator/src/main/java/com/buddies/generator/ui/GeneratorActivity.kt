package com.buddies.generator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.buddies.common.util.getStringOrNull
import com.buddies.common.util.load
import com.buddies.common.util.observe
import com.buddies.generator.R
import com.buddies.generator.databinding.ActivityGeneratorBinding
import com.buddies.generator.viewmodel.GeneratorViewModel
import com.buddies.generator.viewmodel.GeneratorViewModel.Action
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.CopyToClipboard
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.Generate
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.GenerateNewValue
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.InputChanged
import com.buddies.generator.viewmodel.GeneratorViewModel.Action.ShareQRCode
import com.buddies.generator.viewstate.GeneratorViewEffect.SetNewValue
import com.buddies.generator.viewstate.GeneratorViewEffect.ShareImage
import com.buddies.generator.viewstate.GeneratorViewEffect.ShowMessage
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeneratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneratorBinding

    private val viewModel: GeneratorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGeneratorBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        setupViews()
        bindViews()
    }

    private fun setupViews() = with (binding) {
        generateNewTagValueButton.setOnClickListener {
            perform(GenerateNewValue)
        }

        newTagCopyButton.setOnClickListener {
            perform(CopyToClipboard(newTagNumber.text.toString()))
        }

        newTagCryptCopyButton.setOnClickListener {
            perform(CopyToClipboard(newTagCryptNumber.text.toString()))
        }

        generateButton.setOnClickListener {
            perform(Generate(input.text.toString(), resources.getDimensionPixelSize(R.dimen.generated_qr_size)))
        }

        addToDbButton.setOnClickListener {
            perform(Action.AddToDB)
        }

        input.doOnTextChanged { text, _, _, _ ->
            perform(InputChanged(text.toString()))
        }

        shareQrButton.setOnClickListener {
            perform(ShareQRCode)
        }

        inputLayout.requestFocus()
    }

    private fun bindViews() = with (binding) {
        observe(viewModel.getStateStream()) {
            generateButton.isEnabled = it.enableGenerateButton
            addToDbButton.isEnabled = it.enableAddButton
            shareQrButton.isEnabled = it.enableShareButton
            newTagCopyButton.isEnabled = it.enableCopyButtons
            newTagCryptCopyButton.isEnabled = it.enableCopyButtons
            syncProgress.isVisible = it.syncProgress
            qrProgress.isVisible = it.generateProgress
            qrResult.load(it.generatedQrTag, this@GeneratorActivity)
            newTagNumber.text = it.generatedValue
            newTagCryptNumber.text = it.generatedEncrypted
            inputLayout.error = getStringOrNull(it.error)
        }

        observe(viewModel.getEffectStream()) {
            when (it) {
                is SetNewValue -> input.setText(it.value)
                is ShareImage -> shareImage(it.intent)
                is ShowMessage -> showMessage(it.message)
            }
        }
    }

    private fun shareImage(intent: Intent) {
        startActivity(Intent.createChooser(intent, getString(R.string.share_qr_message)))
    }

    private fun showMessage(message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun perform(action: Action) {
        viewModel.perform(action)
    }
}