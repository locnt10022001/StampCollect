package com.stampcollect.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object StampShareHelper {

    fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String = "stamp_collection.png") {
        try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "com.stampcollect.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Stamp Collection"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Legacy method to share a single stamp image file
     */
    fun shareStampFile(context: Context, imagePath: String) {
        try {
            val file = File(imagePath)
            if (!file.exists()) return

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "com.stampcollect.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Stamp"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
