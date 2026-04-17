package com.stampcollect.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

object StampImageHelper {

    fun processAndSaveStamp(
        imageProxy: ImageProxy,
        outputFile: File,
        frameType: String = "Classic"
    ): String {
        val bitmap = imageProxyToBitmap(imageProxy)
        val rotated = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())

        // Crop region to match the 72% wide, 1.3× aspect frame visible in camera
        val cropWidth = (rotated.width * 0.72f).toInt()
        val cropHeight = (cropWidth * 1.3f).toInt()
        val cropLeft = (rotated.width - cropWidth) / 2
        val cropTop  = (rotated.height - cropHeight) / 2
        val cropped  = Bitmap.createBitmap(rotated, cropLeft, cropTop, cropWidth, cropHeight)

        val stampified = applyFrame(cropped, frameType)

        FileOutputStream(outputFile).use { out ->
            stampified.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        imageProxy.close()
        return outputFile.absolutePath
    }

    fun processAndSaveFromUri(
        context: Context,
        uri: Uri,
        outputFile: File,
        frameType: String = "Classic"
    ): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return ""
        
        // Correct orientation from EXIF if needed (omitted for brevity, assume simple)
        // Center crop to 1.3 aspect ratio
        val targetAspect = 1.0f / 1.3f
        val currentAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        
        val (cropW, cropH) = if (currentAspect > targetAspect) {
            (bitmap.height * targetAspect).toInt() to bitmap.height
        } else {
            bitmap.width to (bitmap.width / targetAspect).toInt()
        }
        
        val left = (bitmap.width - cropW) / 2
        val top = (bitmap.height - cropH) / 2
        val cropped = Bitmap.createBitmap(bitmap, left, top, cropW, cropH)
        
        val stampified = applyFrame(cropped, frameType)
        
        FileOutputStream(outputFile).use { out ->
            stampified.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return outputFile.absolutePath
    }

    private fun applyFrame(bitmap: Bitmap, frameType: String): Bitmap {
        return when (frameType) {
            "Scalloped" -> addScallopedFrame(bitmap)
            "Modern" -> addModernFrame(bitmap)
            else -> addRealisticStampFrame(bitmap)
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun addRealisticStampFrame(original: Bitmap): Bitmap {
        val iw = original.width.toFloat()
        val ih = original.height.toFloat()

        val border    = iw * 0.09f
        val perfR     = border * 0.52f
        val perfStep  = perfR * 2.45f
        val midPerf   = border / 2f

        val totalW = (iw + border * 2).toInt()
        val totalH = (ih + border * 2).toInt()

        val result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF9F0") }
        canvas.drawRect(0f, 0f, totalW.toFloat(), totalH.toFloat(), bgPaint)

        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(30, 0, 0, 0)
            maskFilter = BlurMaskFilter(border * 0.4f, BlurMaskFilter.Blur.INNER)
        }
        canvas.drawRect(border, border, border + iw, border + ih, shadowPaint)

        canvas.drawBitmap(original, border, border, null)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(60, 100, 80, 60)
            strokeWidth = (iw * 0.004f).coerceAtLeast(1f)
            style = Paint.Style.STROKE
        }
        canvas.drawRect(border, border, border + iw, border + ih, linePaint)

        val perfPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        var x = perfStep / 2f
        while (x <= totalW) {
            canvas.drawCircle(x, midPerf, perfR, perfPaint)
            canvas.drawCircle(x, totalH - midPerf, perfR, perfPaint)
            x += perfStep
        }
        var y = perfStep / 2f
        while (y <= totalH) {
            canvas.drawCircle(midPerf, y, perfR, perfPaint)
            canvas.drawCircle(totalW - midPerf, y, perfR, perfPaint)
            y += perfStep
        }

        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(40, 100, 80, 50)
            strokeWidth = (iw * 0.003f).coerceAtLeast(1f)
            style = Paint.Style.STROKE
        }
        canvas.drawRect(0f, 0f, totalW.toFloat(), totalH.toFloat(), edgePaint)

        return result
    }

    private fun addScallopedFrame(original: Bitmap): Bitmap {
        val iw = original.width.toFloat()
        val ih = original.height.toFloat()

        val border = iw * 0.1f
        val perfR = border * 0.4f
        val perfStep = perfR * 2.2f
        
        val totalW = (iw + border * 2).toInt()
        val totalH = (ih + border * 2).toInt()

        val result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Draw scallop edge layout
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF5E1") }
        
        // Base rectangle
        canvas.drawRect(border / 2f, border / 2f, totalW - border / 2f, totalH - border / 2f, bgPaint)
        
        // Scallops
        var x = perfStep / 2f
        while (x <= totalW) {
            canvas.drawCircle(x, border / 4f, perfR, bgPaint)
            canvas.drawCircle(x, totalH - border / 4f, perfR, bgPaint)
            x += perfStep
        }
        var y = perfStep / 2f
        while (y <= totalH) {
            canvas.drawCircle(border / 4f, y, perfR, bgPaint)
            canvas.drawCircle(totalW - border / 4f, y, perfR, bgPaint)
            y += perfStep
        }

        canvas.drawBitmap(original, border, border, null)

        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(50, 130, 90, 60)
            strokeWidth = (iw * 0.005f).coerceAtLeast(1f)
            style = Paint.Style.STROKE
        }
        canvas.drawRect(border - 2f, border - 2f, border + iw + 2f, border + ih + 2f, edgePaint)

        return result
    }

    private fun addModernFrame(original: Bitmap): Bitmap {
        val iw = original.width.toFloat()
        val ih = original.height.toFloat()

        val borderX = iw * 0.05f
        val borderY = ih * 0.15f
        
        val totalW = (iw + borderX * 2).toInt()
        val totalH = (ih + borderY * 2).toInt()

        val result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Clean white background (Polaroid style)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        val rect = RectF(0f, 0f, totalW.toFloat(), totalH.toFloat())
        val corner = totalW * 0.02f
        canvas.drawRoundRect(rect, corner, corner, bgPaint)

        canvas.drawBitmap(original, borderX, borderX, null) // Leave more space at bottom

        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(25, 0, 0, 0)
            strokeWidth = (iw * 0.002f).coerceAtLeast(1f)
            style = Paint.Style.STROKE
        }
        canvas.drawRoundRect(rect, corner, corner, edgePaint)

        return result
    }
}
