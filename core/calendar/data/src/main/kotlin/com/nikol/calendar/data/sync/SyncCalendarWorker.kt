package com.nikol.calendar.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.calendar.data.remote.CalDavService
import com.nikol.sync.Syncable
import javax.inject.Inject

class SyncCalendarWorker (
    calendarSyncRepo: Syncable,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        TODO()
    }
}