package com.buddies.scanner.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.buddies.common.util.CameraHelper
import com.buddies.common.util.inflater
import com.buddies.common.util.observe
import com.buddies.scanner.R
import com.buddies.scanner.databinding.QrScannerBinding
import com.buddies.scanner.ml.QRCodeAnalyzer
import com.buddies.scanner.viewmodel.ScannerViewModel
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.CloseScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.HandleResult
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.StartScanner
import com.buddies.scanner.viewmodel.ScannerViewModel.Action.ValidateScan
import com.buddies.scanner.viewstate.ScannerViewEffect.StartCamera
import com.buddies.scanner.viewstate.ScannerViewEffect.StopCamera
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.getViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@androidx.camera.core.ExperimentalGetImage
class QRScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding: QrScannerBinding

    private var viewModel: ScannerViewModel? = null
    private var currentScan: DisposableHandle? = null

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

    fun setResultMessage(message: String) {
        if (message.isNotEmpty()) binding.qrResult.text = message
    }

    fun scan(
        fragment: Fragment,
        cameraHelper: CameraHelper,
        initResult: String = ""
    ) = liveData(fragment.lifecycleScope.coroutineContext) {

        viewModel = fragment.getViewModel()
        val localViewModel = viewModel

        if (localViewModel != null) {

            val analyzer = QRCodeAnalyzer(fragment)

            binding.scanAgainButton.setOnClickListener {
                localViewModel.perform(StartScanner)
            }

            fragment.observe(localViewModel.viewState) {
                with(binding) {
                    isVisible = it.showScanner
                    progress.isVisible = it.showLoading
                    scanAgainButton.isVisible = it.showScanAgainButton
                    qrResult.text = context.resources.getString(it.message)
                }
            }

            fragment.observe(localViewModel.viewEffect) {
                when (it) {
                    is StartCamera -> startCamera(cameraHelper, analyzer)
                    is StopCamera -> stopCamera(cameraHelper)
                }
            }

            localViewModel.perform(StartScanner)
            localViewModel.perform(HandleResult(initResult))

            currentScan = emitSource(map(localViewModel.viewState) {
                it.result
            }.distinctUntilChanged())

            analyzer.barcodes.collectLatest { barcode ->
                localViewModel.perform(ValidateScan(barcode.displayValue))
            }
        }
    }

    fun stopScan() {
        viewModel?.perform(CloseScanner)
        currentScan?.dispose()
    }

    private fun startCamera(cameraHelper: CameraHelper, analyzer: QRCodeAnalyzer) {
        cameraHelper.startCamera(
            context,
            binding.preview,
            analyzer
        )
    }

    private fun stopCamera(cameraHelper: CameraHelper) {
        cameraHelper.stopCamera(context)
    }
}