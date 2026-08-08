package com.nikol.lms.backround

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    NOT_DOWNLOADED,
    PENDING,
    DOWNLOADING,
    COMPLETED,
    FAILED
}

@Entity(tableName = "course_files")
data class FileEntity(
    @PrimaryKey val id: String, // name_version
    val fileName: String,
    val version: String,
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
)