package com.nikol.lms.backround

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

fun startSingleFileWork(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<DownloadResWorker>()
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "single_file_processing_work",
        ExistingWorkPolicy.KEEP,
        workRequest
    )
}

fun openDownloadedFile(context: Context, uriString: String, mimeType: String?) {
    try {
        val uri = Uri.parse(uriString)
        val finalMimeType = mimeType ?: context.contentResolver.getType(uri) ?: "*/*"

        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, finalMimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(openIntent, "Открыть файл"))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Не удалось открыть файл", android.widget.Toast.LENGTH_SHORT).show()
    }
}