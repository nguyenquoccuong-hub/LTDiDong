package com.example.blur_o_matic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data
import java.io.File
import java.io.FileOutputStream

class BlurWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {
            val imageUri = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()
            val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)
            val bitmap = BitmapFactory.decodeFile(imageUri)
            val blurredBitmap = blurBitmap(bitmap, blurLevel)
            val outputFile = File(applicationContext.cacheDir, "blurred_${System.currentTimeMillis()}.png")
            FileOutputStream(outputFile).use { out ->
                blurredBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val outputData = Data.Builder()
                .putString(KEY_IMAGE_URI, outputFile.absolutePath)
                .build()
            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun blurBitmap(bitmap: Bitmap, blurLevel: Int): Bitmap {
        return try {
            // Sử dụng RenderScript để làm mờ hiệu quả
            val rs = RenderScript.create(applicationContext)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createTyped(rs, input.type)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            // Tính bán kính blur dựa trên mức độ mờ (1-3)
            val radius = when (blurLevel) {
                1 -> 10f  // Mờ nhẹ
                2 -> 20f  // Mờ hơn
                else -> 25f  // Mờ nhiều nhất
            }

            script.setRadius(radius)
            script.setInput(input)
            script.forEach(output)

            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
            output.copyTo(result)

            script.destroy()
            output.destroy()
            input.destroy()
            rs.destroy()

            result
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: simple blur nếu RenderScript không khả dụng
            simpleBitmap(bitmap, blurLevel)
        }
    }

    private fun simpleBitmap(bitmap: Bitmap, blurLevel: Int): Bitmap {
        // Fallback blur method
        val width = bitmap.width
        val height = bitmap.height
        val blurred = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(blurred)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.alpha = (255 / (blurLevel + 1))
        for (i in 0..blurLevel) {
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        }
        return blurred
    }

    companion object {
        const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
        const val KEY_BLUR_LEVEL = "KEY_BLUR_LEVEL"
    }
}
