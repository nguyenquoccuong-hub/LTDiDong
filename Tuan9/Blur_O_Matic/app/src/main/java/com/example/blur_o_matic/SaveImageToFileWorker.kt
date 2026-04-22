package com.example.blur_o_matic

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToFileWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val resolver = applicationContext.contentResolver
        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeFile(resourceUri)
            
            val title = "Blurred_Image_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
            val uri = saveImageToGallery(bitmap, title)

            if (uri != null) {
                val outputData = Data.Builder()
                    .putString(KEY_IMAGE_URI, uri.toString())
                    .build()
                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap, title: String): Uri? {
        val filename = "$title.png"
        var out: OutputStream? = null
        var imageUri: Uri? = null
        val resolver = applicationContext.contentResolver

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/BlurOMatic")
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val imagesDir = applicationContext.getExternalFilesDir(null)?.absolutePath
                val image = File(imagesDir, filename)
                imageUri = Uri.fromFile(image)
            }

            imageUri?.let {
                out = resolver.openOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out!!)
            }
            return imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            out?.close()
        }
    }

    companion object {
        const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
    }
}
