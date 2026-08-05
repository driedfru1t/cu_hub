package com.nikol.lms.domain.repo

import arrow.core.Either
import com.nikol.lms.domain.error.StorageError
import com.nikol.lms.domain.model.UploadMetaData

interface StorageRepository {

    /**
     * [Эндпоинт 17] Получение временной presigned URL-ссылки для скачивания файла.
     * GET /micro-lms/content/download-link
     */
    suspend fun getDownloadLink(filename: String, version: String?): Either<StorageError, String>

    /**
     * [Эндпоинт 18] Получение ссылки и метаданных для загрузки (Upload) файла.
     * GET /micro-lms/content/upload-link
     */
    suspend fun getUploadLink(
        filename: String,
        directory: String,
        contentType: String
    ): Either<StorageError, UploadMetaData>

    /**
     * [Эндпоинт 19] Физическая загрузка байт файла в хранилище (по ссылке из п.18).
     * PUT {upload_url}
     */
    suspend fun uploadFile(
        uploadUrl: String,
        contentType: String,
        fileBytes: ByteArray,
        version: String? = null
    ): Either<StorageError, Unit>
}