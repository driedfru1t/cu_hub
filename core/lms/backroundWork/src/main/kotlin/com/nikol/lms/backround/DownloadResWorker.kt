package com.nikol.lms.backround

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.sync.SyncResult
import io.ktor.client.HttpClient
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http

interface DownloadService {
    suspend fun download(): SyncResult
}

internal class DownloadCuResService @Inject constructor(
    @param:Http(CuHttpClient.CU) private val cuClient: HttpClient,
) : DownloadService {
    override suspend fun download(): SyncResult {
        TODO("Not yet implemented")
    }
}

class DownloadResWorker @Inject constructor(
    private val downloadService: DownloadService,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return Result.success()
    }
}