package com.nikol.lms.data

import arrow.core.Either
import com.nikol.lms.domain.error.StorageError
import com.nikol.lms.domain.model.UploadMetaData
import com.nikol.lms.domain.repo.StorageRepository

class StorageRepositoryImpl : StorageRepository {
    /**
     * [Эндпоинт 17] Получение временной presigned URL-ссылки для скачивания файла.
     * GET /micro-lms/content/download-link
     */
    override suspend fun getDownloadLink(
        filename: String,
        version: String?
    ): Either<StorageError, String> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 18] Получение ссылки и метаданных для загрузки (Upload) файла.
     * GET /micro-lms/content/upload-link
     */
    override suspend fun getUploadLink(
        filename: String,
        directory: String,
        contentType: String
    ): Either<StorageError, UploadMetaData> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 19] Физическая загрузка байт файла в хранилище (по ссылке из п.18).
     * PUT {upload_url}
     */
    override suspend fun uploadFile(
        uploadUrl: String,
        contentType: String,
        fileBytes: ByteArray,
        version: String?
    ): Either<StorageError, Unit> {
        TODO("Not yet implemented")
    }
}