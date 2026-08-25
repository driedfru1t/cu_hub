package com.nikol.lms.backround

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDAO {
    @Query("SELECT * FROM files WHERE status = :status")
    suspend fun getFilesByStatusList(status: DownloadStatus): List<FileEntity>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFileById(id: String): FileEntity?

    @Query("SELECT * FROM files WHERE id IN (:ids)")
    fun getFileByIdFlow(ids: List<String>): Flow<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(files: List<FileEntity>)

    @Query("UPDATE files SET status = :status WHERE id IN (:ids)")
    suspend fun updateStatus(ids: List<String>, status: DownloadStatus)

    @Query("""
        UPDATE files 
        SET status = :status, localUri = :localUri, mimeType = :mimeType 
        WHERE id = :id
    """)
    suspend fun updateDownloadSuccess(
        id: String,
        status: DownloadStatus = DownloadStatus.COMPLETED,
        localUri: String,
        mimeType: String
    )
}