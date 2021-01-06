package com.buddies.common.util

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Size
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraHelper(
    private var fragment: Fragment?,
    private val photoSize: Size = Size(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT),
    private var lensFacing: Int = LENS_FACING_BACK
) : DefaultLifecycleObserver {

    private var cameraPermissionsRequest: ActivityResultLauncher<String>? = null
    private var cameraLaunchRequest: ActivityResultLauncher<Uri>? = null

    private var executor: ExecutorService? = null

    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null

    private var localAction: () -> Unit = {}
    private var cameraAction: (Uri) -> Unit = {}

    private var uri = Uri.EMPTY

    init {
        fragment?.lifecycle?.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        fragment?.let {
            cameraPermissionsRequest = it.registerForTrueActivityResult(RequestPermission()) {
                localAction.invoke()
            }
            cameraLaunchRequest = it.registerForTrueActivityResult(TakePicture()) {
                cameraAction.invoke(uri)
            }
        }
    }

    private fun launchPermissionRequest() {
        cameraPermissionsRequest?.launch(Manifest.permission.CAMERA)
    }

    private fun launchCameraRequest(context: Context) {
        uri = getNewUri(context)
        cameraLaunchRequest?.launch(uri)
    }

    private fun startCameraAction(
        context: Context,
        previewView: PreviewView,
        analyser: ImageAnalysis.Analyzer? = null,
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener({
            bindPreview(previewView, providerFuture.get(), analyser)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun switchLensAction(
        context: Context,
        previewView: PreviewView,
        analyser: ImageAnalysis.Analyzer? = null,
    ) {
        lensFacing = if (lensFacing == LENS_FACING_BACK) LENS_FACING_FRONT else LENS_FACING_BACK
        startCameraAction(context, previewView, analyser)
    }

    fun startCamera(
        context: Context,
        previewView: PreviewView,
        analyser: ImageAnalysis.Analyzer? = null,
    ) {
        localAction = { startCameraAction(context, previewView, analyser) }
        launchPermissionRequest()
    }

    fun stopCamera(context: Context) {
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener({
            unbindPreview(providerFuture.get())
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchLens(
        context: Context,
        previewView: PreviewView,
        analyser: ImageAnalysis.Analyzer? = null,
    ) {
        localAction = { switchLensAction(context, previewView, analyser) }
        launchPermissionRequest()
    }

    fun launchCamera(
        context: Context,
        action: (Uri) -> Unit
    ) {
        cameraAction = action
        localAction = { launchCameraRequest(context) }
        launchPermissionRequest()
    }

    suspend fun takePicture(
        context: Context
    ) = suspendCoroutine<File> { cont ->

        val newFile = getNewFile(context)

        val savedListener = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                cont.resume(newFile)
            }

            override fun onError(exception: ImageCaptureException) {
                cont.resumeWithException(exception)
            }
        }

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = lensFacing == LENS_FACING_FRONT
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(newFile)
            .setMetadata(metadata)
            .build()

        imageCapture?.takePicture(outputFileOptions, getOrCreateExecutor(), savedListener)
    }

    private fun bindPreview(
        previewView: PreviewView,
        cameraProvider: ProcessCameraProvider,
        analyser: ImageAnalysis.Analyzer? = null,
    ) {
        fragment?.let {
            val preview = Preview.Builder().apply {
                //setTargetResolution(photoSize)
            }.build()

            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            analyser?.let {
                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(photoSize)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().apply {
                        setAnalyzer(getOrCreateExecutor(), it)
                    }
            }

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                it,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )

            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    private fun unbindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
    }

    private fun getOrCreateExecutor(): ExecutorService =
        executor ?: Executors.newSingleThreadExecutor().apply { executor = this }

    private fun getNewUri(
        context: Context
    ) = getUriForFile(context, "com.buddies.fileprovider", getNewFile(context))

    private fun getNewFile(
        context: Context
    ) = File.createTempFile(
        "${System.currentTimeMillis()}",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    ).apply {
        deleteOnExit()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        executor?.shutdown()
        executor = null
        fragment?.lifecycle?.removeObserver(this)
        fragment = null
    }

    companion object {
        private const val DEFAULT_IMAGE_HEIGHT = 1920
        private const val DEFAULT_IMAGE_WIDTH = 1080
    }
}