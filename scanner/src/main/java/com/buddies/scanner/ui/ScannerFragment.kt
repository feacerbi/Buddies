package com.buddies.scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.PermissionsManager
import com.buddies.common.util.observe
import com.buddies.common.util.safeLaunch
import com.buddies.scanner.databinding.FragmentScannerBinding
import com.buddies.scanner.ml.QRCodeAnalyzer
import com.buddies.security.encryption.Encrypter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import java.util.*

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class ScannerFragment : Fragment() {

    private lateinit var binding: FragmentScannerBinding

    private val permissionsManager by inject<PermissionsManager>()
    private val encrypter by inject<Encrypter>()

    private val qrAnalyzer by lazy {
        QRCodeAnalyzer(this)
    }

    private val cameraHelper by lazy {
        CameraHelper(
            this,
            binding.preview,
            qrAnalyzer
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentScannerBinding.inflate(layoutInflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        bindViews()
    }

    private fun setUpViews() = with (binding) {
        preview.scaleType = FILL_CENTER
        tryStartCamera()
    }

    private fun bindViews() = with (binding) {
        observe(qrAnalyzer.barcodes) {
            lifecycleScope.safeLaunch {
                val text = "Normal: ${it.displayValue}\n" +
                        "Encrypted: ${encrypter.encrypt(it.displayValue)}\n" +
                        "Decrypted: ${encrypter.decrypt(encrypter.encrypt(it.displayValue))}\n" +
                        "Time: ${Calendar.getInstance().time}"
                result.text = text
            }
        }
    }

    private fun tryStartCamera() {
        if (permissionsManager.cameraPermissionsGranted()) {
            startCamera()
        } else {
            requestCameraPermissions()
        }
    }

    private fun startCamera() {
        cameraHelper.startCamera(requireContext())
    }

    private fun requestCameraPermissions() {
        permissionsManager.requestCameraPermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestResult(requestCode,
            { startCamera() },
            { showMessage(it) })
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}