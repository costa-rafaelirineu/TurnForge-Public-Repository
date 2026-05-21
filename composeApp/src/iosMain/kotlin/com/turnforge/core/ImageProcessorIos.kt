package com.turnforge.core

import androidx.compose.ui.geometry.Rect
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.writeToURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
actual suspend fun cropImage(uri: String, cropRect: Rect): String = withContext(Dispatchers.IO) {
    val url = if (uri.startsWith("file://") || uri.startsWith("/")) {
        if (uri.startsWith("/")) NSURL.fileURLWithPath(uri) else NSURL.URLWithString(uri)
    } else {
        NSURL.URLWithString(uri)
    } ?: return@withContext uri

    val originalImage = UIImage.imageWithContentsOfFile(url.path ?: "") ?: return@withContext uri

    // Fix orientation by drawing into a new context
    val normalizedImage = originalImage.fixOrientation()
    val cgImage = normalizedImage.CGImage ?: return@withContext uri

    val width = CGImageGetWidth(cgImage).toDouble()
    val height = CGImageGetHeight(cgImage).toDouble()

    // Map normalized rect to actual pixel coordinates
    val left = cropRect.left.toDouble() * width
    val top = cropRect.top.toDouble() * height
    val cropWidth = cropRect.width.toDouble() * width
    val cropHeight = cropRect.height.toDouble() * height

    val rect = CGRectMake(left, top, cropWidth, cropHeight)
    val croppedCgImage = CGImageCreateWithImageInRect(cgImage, rect) ?: return@withContext uri
    val croppedImage = UIImage.imageWithCGImage(croppedCgImage)

    val imageData = UIImageJPEGRepresentation(croppedImage, 0.9) ?: return@withContext uri

    val fileName = "cropped_${NSUUID.UUID().UUIDString}.jpg"
    val fileUrl = NSURL.fileURLWithPath(NSTemporaryDirectory() + fileName)

    if (imageData.writeToURL(fileUrl, true)) {
        fileUrl.absoluteString ?: uri
    } else {
        uri
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.fixOrientation(): UIImage {
    val size = this.size
    UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
    this.drawInRect(CGRectMake(0.0, 0.0, size.useContents { width }, size.useContents { height }))
    val normalizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return normalizedImage ?: this
}
