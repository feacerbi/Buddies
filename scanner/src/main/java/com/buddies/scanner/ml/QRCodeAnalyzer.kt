package com.buddies.scanner.ml

import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.buddies.common.util.ActionTimer
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class QRCodeAnalyzer(
    lifecycleOwner: LifecycleOwner
) : ImageAnalysis.Analyzer, DefaultLifecycleObserver {

    private val barcodesMutable = ConflatedBroadcastChannel<Barcode>()
    val barcodes = barcodesMutable.asFlow()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val timer = ActionTimer(lifecycleOwner.lifecycleScope, DEBOUNCE_TIMEOUT)

    private val scanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        BarcodeScanning.getClient(options)
    }

    override fun analyze(imageProxy: ImageProxy) {
        if (timer.available) {
            timer.debounce()
            val mediaImage: Image? = imageProxy.image

            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(inputImage)
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
                    .addOnSuccessListener {
                        if (it.size > 0) {
                            barcodesMutable.offer(it[0])
                        }
                    }
                return
            }
        }

        imageProxy.close()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        barcodesMutable.close()
        scanner.close()
    }

    companion object {
        private const val DEBOUNCE_TIMEOUT = 1000L
    }
}