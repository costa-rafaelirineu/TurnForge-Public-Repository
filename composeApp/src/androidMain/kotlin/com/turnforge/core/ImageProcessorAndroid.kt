package com.turnforge.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.net.toUri

actual suspend fun cropImage(uri: String, cropRect: Rect): String = withContext(Dispatchers.IO) {
    try {
        val context = ContextProvider.getContext()
        val contentResolver = context.contentResolver
        val imageUri = uri.toUri()

        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) return@withContext uri

        // Fix orientation from EXIF
        val orientation = try {
            contentResolver.openInputStream(imageUri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

        val matrix = Matrix().apply {
            val rotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            if (rotation != 0f) postRotate(rotation)
        }

        val rotatedBitmap = if (!matrix.isIdentity) {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
                if (it != bitmap) bitmap.recycle()
            }
        } else {
            bitmap
        }

        val left = (cropRect.left * rotatedBitmap.width).toInt().coerceIn(0, rotatedBitmap.width - 1)
        val top = (cropRect.top * rotatedBitmap.height).toInt().coerceIn(0, rotatedBitmap.height - 1)
        val width = (cropRect.width * rotatedBitmap.width).toInt().coerceAtMost(rotatedBitmap.width - left)
        val height = (cropRect.height * rotatedBitmap.height).toInt().coerceAtMost(rotatedBitmap.height - top)

        if (width <= 0 || height <= 0) {
            if (!rotatedBitmap.isRecycled) rotatedBitmap.recycle()
            return@withContext uri
        }

        val croppedBitmap = Bitmap.createBitmap(rotatedBitmap, left, top, width, height)
        if (croppedBitmap != rotatedBitmap) rotatedBitmap.recycle()

        val tempFile = File(context.cacheDir, "cropped_${UUID.randomUUID()}.jpg")
        FileOutputStream(tempFile).use { out ->
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        croppedBitmap.recycle()

        Uri.fromFile(tempFile).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        uri
    }
}
