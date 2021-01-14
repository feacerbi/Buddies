package com.buddies.generator.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.buddies.common.util.TAG_PREFIX
import com.buddies.common.util.newImageFile
import com.buddies.common.util.toUri
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream

class QRCodeHelper(
    private val appContext: Context
) {

    fun createQR(
        data: String,
        height: Int,
        width: Int
    ): Uri {
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to Charsets.UTF_8.toString(),
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )

        val matrix = QRCodeWriter().encode(
            TAG_PREFIX + data,
            BarcodeFormat.QR_CODE,
            width,
            height,
            hints
        )

        val file = appContext.newImageFile().apply {
            writeToFile(matrix, height, width, this)
        }

        return file.toUri(appContext)
    }

    private fun writeToFile(
        bitMatrix: BitMatrix,
        height: Int,
        width: Int,
        file: File
    ) {
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val isPixelBlack = bitMatrix.get(x, y)
                pixels[y * width + x] = if (isPixelBlack) Color.BLACK else Color.WHITE
            }
        }

        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
            compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        }
    }
}