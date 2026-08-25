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

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val name: String,
    val version: String?,
    val size: Long,
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val localUri: String? = null,
    val mimeType: String? = null
)

fun formatFileName(originalName: String, version: String?): String {
    val extension = originalName.substringAfterLast('.', "")
    val baseName = originalName.substringBeforeLast('.')
    return if (version.isNullOrBlank()) {
        originalName
    } else if (extension.isNotEmpty() && extension != originalName) {
        "${baseName}_v${version}.${extension}"
    } else {
        "${originalName}_v${version}"
    }
}