package com.example.blur_o_matic

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class CleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val cacheDir = applicationContext.cacheDir
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("blurred_")) {
                file.delete()
            }
        }
        return Result.success()
    }
}

