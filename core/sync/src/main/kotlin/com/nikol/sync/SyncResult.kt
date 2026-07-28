package com.nikol.sync

import androidx.work.ListenableWorker.Result

sealed interface SyncResult {
    data object Success : SyncResult
    data object Retry : SyncResult
    data object Failure : SyncResult
}

fun SyncResult.toWorkResult(): Result =
    when (this) {
        SyncResult.Failure -> Result.success()
        SyncResult.Retry -> Result.retry()
        SyncResult.Success -> Result.failure()
    }
