package com.buddies.common.util

import android.content.Context
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraHelper(
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val analyser: ImageAnalysis.Analyzer? = null,
    private val photoSize: Size = Size(
        DEFAULT_IMAGE_WIDTH,
        DEFAULT_IMAGE_HEIGHT
    ),
    private var lensFacing: Int = LENS_FACING_BACK
): LifecycleObserver {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var currentFile = getNewFile()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun startCamera(context: Context) {
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener(Runnable {
            bindPreview(providerFuture.get())
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera(context: Context) {
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener(Runnable {
            unbindPreview(providerFuture.get())
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchLens(context: Context) {
        lensFacing = if (lensFacing == LENS_FACING_BACK) LENS_FACING_FRONT else LENS_FACING_BACK
        startCamera(context)
    }

    suspend fun takePicture() = suspendCoroutine<File> { cont ->

        val savedListener = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                cont.resume(currentFile)
            }

            override fun onError(exception: ImageCaptureException) {
                cont.resumeWithException(exception)
            }
        }

        currentFile = getNewFile()

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = lensFacing == LENS_FACING_FRONT
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(currentFile)
            .setMetadata(metadata)
            .build()

        imageCapture?.takePicture(outputFileOptions, executor, savedListener)
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().apply {
            setTargetResolution(photoSize)
        }.build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        if (analyser != null) {
            imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(photoSize)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().apply {
                    setAnalyzer(executor, analyser)
                }
        }

        cameraProvider.unbindAll()

        camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )

        preview.setSurfaceProvider(previewView.createSurfaceProvider())
    }

    private fun unbindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
    }

    @OnLifecycleEvent(ON_STOP)
    private fun clean() {
        executor.shutdown()
    }

    private fun getNewFile() = File(
        previewView.context.externalMediaDirs.first(),
        "${System.currentTimeMillis()}.jpg"
    )

    companion object {
        private const val DEFAULT_IMAGE_HEIGHT = 1920
        private const val DEFAULT_IMAGE_WIDTH = 1080
    }
}