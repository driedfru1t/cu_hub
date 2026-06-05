package com.nikol.lms.domain.model

import com.nikol.lms.domain.common.UnstableLmsApi

// [!] Спецификация отсутствует в OpenAPI. GET /micro-lms/content/upload-link.
// Поля определены гипотетически на основе стандартных S3 S-Presigned Upload API.
@UnstableLmsApi
data class UploadMetaData(
    val url: String, // Куда нужно делать PUT-запрос
    val filename: String,
    val directory: String,
    val headers: Map<String, String> // Необходимые HTTP заголовки для загрузки (например, x-amz-meta-version)
)
