package com.buddies.scanner.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.inflater
import com.buddies.common.util.toDefaultError
import com.buddies.scanner.R
import com.buddies.scanner.databinding.QrScannerBinding
import com.buddies.scanner.ml.QRCodeAnalyzer
import com.buddies.security.encryption.Encrypter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent.inject

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class QRScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding: QrScannerBinding

    private val encrypter by inject(Encrypter::class.java)

    init {
        QrScannerBinding.inflate(inflater(), this, true).apply {
            binding = this
        }

        val styleAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.QRScanner, defStyle, 0
        )

        setup(styleAttrs)
        styleAttrs.recycle()
    }

    private fun setup(styleAttrs: TypedArray) = with (binding) {
        preview.scaleType = FILL_CENTER
    }

    fun scan(
        lifecycleOwner: LifecycleOwner,
        cameraHelper: CameraHelper
    ) = liveData(lifecycleOwner.lifecycleScope.coroutineContext) {

        val qrCodeAnalyzer = QRCodeAnalyzer(lifecycleOwner)

        cameraHelper.startCamera(
            context,
            binding.preview,
            qrCodeAnalyzer
        )

        qrCodeAnalyzer.barcodes.collectLatest { barcode ->
            try {
                val decodedBarcode = encrypter.decrypt(barcode.displayValue)
                emit(Success(decodedBarcode))
            } catch (e: Exception) {
                emit(Fail<String>(e.toDefaultError()))
            }
        }
    }
}