package com.nikol.storage.db

import androidx.room.TypeConverter
import com.nikol.lms.backround.DownloadStatus
import java.time.Instant

class CalendarConverters {

    @TypeConverter
    fun instantToLong(value: Instant?): Long? {
        return value?.toEpochMilli()
    }

    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromInstantList(value: List<Instant>?): String? {
        return value?.joinToString(",") { it.toEpochMilli().toString() }
    }

    @TypeConverter
    fun toInstantList(value: String?): List<Instant> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").mapNotNull { epochStr ->
            epochStr.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        }
    }

    @TypeConverter
    fun fromDownloadStatus(state: DownloadStatus) = state.name

    @TypeConverter
    fun toDownloadStatus(state: String) = DownloadStatus.valueOf(state)
}