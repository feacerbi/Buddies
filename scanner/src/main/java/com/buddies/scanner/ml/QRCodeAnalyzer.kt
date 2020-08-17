package com.buddies.scanner.ml

import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import com.buddies.common.util.ActionTimer
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class QRCodeAnalyzer(
    lifecycleOwner: LifecycleOwner
) : ImageAnalysis.Analyzer, LifecycleObserver {

    private val barcodesMutable = MutableLiveData<Barcode>()
    val barcodes: LiveData<Barcode> = barcodesMutable

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
                            barcodesMutable.value = it[0]
                        }
                    }
                return
            }
        }

        imageProxy.close()
    }

    @OnLifecycleEvent(ON_STOP)
    fun clean() {
        scanner.close()
    }

    companion object {
        private const val DEBOUNCE_TIMEOUT = 1000L
    }
}