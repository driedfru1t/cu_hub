package com.nikol.lms.backround

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import arrow.core.raise.either
import com.nikol.common.CuHubDispatcher
import com.nikol.common.Dispatcher
import com.nikol.sync.di.ChildWorkerFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicInteger

class DownloadResWorker @AssistedInject constructor(
    private val downloadService: DownloadCuResService,
    private val fileDAO: FileDAO,
    @param:Dispatcher(CuHubDispatcher.IO) private val coroutineDispatcher: CoroutineDispatcher,
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val semaphore = Semaphore(3)
    private val channelId = "lms_download_channel"
    private val activeNotificationId = 9999
    private val summaryNotificationId = 10000
    private val groupKey = "com.nikol.lms.DOWNLOADED_GROUP"

    override suspend fun doWork(): Result = withContext(coroutineDispatcher) {
        val orphaned = fileDAO.getFilesByStatusList(DownloadStatus.DOWNLOADING)
        if (orphaned.isNotEmpty()) {
            fileDAO.updateStatus(orphaned.map { it.id }, DownloadStatus.PENDING)
        }

        val initialPending = fileDAO.getFilesByStatusList(DownloadStatus.PENDING)
        if (initialPending.isEmpty()) return@withContext Result.success()

        val processedCount = AtomicInteger(0)
        val downloadedResults = mutableListOf<Pair<FileEntity, Uri>>()

        val getDynamicTotalCount: suspend (Int) -> Int = { processed ->
            val pendingCount = fileDAO.getFilesByStatusList(DownloadStatus.PENDING).size
            val downloadingCount = fileDAO.getFilesByStatusList(DownloadStatus.DOWNLOADING).size
            processed + pendingCount + downloadingCount
        }

        setForeground(createActiveForegroundInfo(processedCount.get(), getDynamicTotalCount(0)))

        while (true) {
            val needDownload = fileDAO.getFilesByStatusList(DownloadStatus.PENDING)
            if (needDownload.isEmpty()) break

            coroutineScope {
                val tasks = needDownload.map { file ->
                    async {
                        semaphore.withPermit {
                            val uri = processSingleFile(file)
                            if (uri != null) {
                                synchronized(downloadedResults) {
                                    downloadedResults.add(file to uri)
                                }
                            }
                            val currentProcessed = processedCount.incrementAndGet()
                            val totalCount = getDynamicTotalCount(currentProcessed)
                            setForeground(createActiveForegroundInfo(currentProcessed, totalCount))
                        }
                    }
                }
                tasks.awaitAll()
            }
        }

        if (downloadedResults.isNotEmpty()) {
            showCompletionGroupNotification(downloadedResults)
        }

        Result.success()
    }

    private suspend fun processSingleFile(file: FileEntity): Uri? {
        fileDAO.updateStatus(listOf(file.id), DownloadStatus.DOWNLOADING)

        var downloadedUri: Uri? = null
        var downloadedMimeType = "application/octet-stream"

        val result = either {
            val urlResponse = downloadService.getLink(
                DownloadParam(
                    filename = file.fileName,
                    version = file.version
                )
            )

            val formattedName = formatFileName(file.name, file.version)

            downloadService.downloadFile(
                url = urlResponse.url,
                createOutputStream = { mimeType ->
                    downloadedMimeType = mimeType
                    val pair = createLmsDownloadStream(context, formattedName, mimeType)
                    downloadedUri = pair.second
                    pair.first
                },
                onProgress = {}
            )
        }

        return result.fold(
            ifLeft = {
                fileDAO.updateStatus(listOf(file.id), DownloadStatus.FAILED)
                null
            },
            ifRight = {
                val finalUri = downloadedUri
                if (finalUri != null) {
                    fileDAO.updateDownloadSuccess(
                        id = file.id,
                        status = DownloadStatus.COMPLETED,
                        localUri = finalUri.toString(),
                        mimeType = downloadedMimeType
                    )
                } else {
                    fileDAO.updateStatus(listOf(file.id), DownloadStatus.FAILED)
                }
                finalUri
            }
        )
    }

    private fun showCompletionGroupNotification(results: List<Pair<FileEntity, Uri>>) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureNotificationChannelCreated(notificationManager)

        val maxVisibleItems = 5
        val displayResults = results.take(maxVisibleItems)
        val remainingCount = results.size - maxVisibleItems

        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle("Скачано файлов: ${results.size}")
            .setSummaryText("LMS Загрузки")

        displayResults.forEach { (file, _) ->
            inboxStyle.addLine(formatFileName(file.name, file.version))
        }

        if (remainingCount > 0) {
            inboxStyle.addLine("… и ещё $remainingCount ф.")
        }

        displayResults.forEach { (file, uri) ->
            val formattedName = formatFileName(file.name, file.version)
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                file.id.hashCode(),
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val childNotification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(formattedName)
                .setContentText("Нажмите, чтобы открыть")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(groupKey)
                .build()

            notificationManager.notify(file.id.hashCode(), childNotification)
        }

        val summaryPendingIntent = PendingIntent.getActivity(
            context,
            summaryNotificationId,
            getOpenExplorerIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val summaryNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Загрузка завершена")
            .setContentText("Успешно скачано: ${results.size} ф.")
            .setStyle(inboxStyle)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setContentIntent(summaryPendingIntent)
            .build()

        notificationManager.notify(summaryNotificationId, summaryNotification)
    }

    private fun getOpenExplorerIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val lmsFolderUri =
                Uri.parse("content://com.android.externalstorage.documents/document/primary:Download%2Flms")
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(lmsFolderUri, "vnd.android.document/directory")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    private fun createActiveForegroundInfo(processed: Int, total: Int): ForegroundInfo {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureNotificationChannelCreated(notificationManager)

        val progressPercent = if (total > 0) (processed * 100) / total else 0
        val textMessage = "Скачано $processed из $total файлов ($progressPercent%)"

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("LMS Загрузка")
            .setContentText(textMessage)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(total, processed, false)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(activeNotificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(activeNotificationId, notification)
        }
    }

    private fun ensureNotificationChannelCreated(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            channelId,
            "Загрузка файлов LMS",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun createLmsDownloadStream(
        context: Context,
        fileNameWithVersion: String,
        mimeType: String
    ): Pair<OutputStream, Uri> {
        val cleanFileName = fileNameWithVersion.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/lms"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, cleanFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }
            val uri: Uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IllegalStateException("Ошибка MediaStore")

            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw IllegalStateException("Ошибка открытия потока")
            Pair(outputStream, uri)
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val lmsFolder = File(downloadsDir, "lms").apply { mkdirs() }
            val targetFile = File(lmsFolder, cleanFileName)
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", targetFile)
            Pair(FileOutputStream(targetFile), uri)
        }
    }

    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): DownloadResWorker
    }
}