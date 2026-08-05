package com.nikol.network

import android.util.Log
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import javax.inject.Inject

data class ImageFileRequest(
    val filename: String,
    val version: String?
)

class SecureImageInterceptor @Inject constructor(
    @param:com.nikol.network.di.qualifers.HttpClient(CuHttpClient.CU) private val httpClient: HttpClient
) : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val data = request.data

        if (data is ImageFileRequest) {
            Log.d(
                "CoilResolver",
                "Intercepted ImageFileRequest for filename: ${data.filename}, v: ${data.version}"
            )

            val temporaryS3Url = fetchS3UrlFromApi(data.filename, data.version)

            val newRequest = request.newBuilder()
                .data(temporaryS3Url)
                .memoryCacheKey("${data.filename}_${data.version}")
                .diskCacheKey("${data.filename}_${data.version}")
                .build()

            return chain.withRequest(newRequest).proceed()
        }

        return chain.proceed()
    }

    @Serializable
    private data class S3UrlResponse(val url: String)

    private suspend fun fetchS3UrlFromApi(filename: String, version: String?): String {
        return try {
            val response = httpClient.get("content/download-link") {
                parameter("filename", filename)
                if (version != null) {
                    parameter("version", version)
                }
            }.body<S3UrlResponse>()

            Log.d("CoilResolver", "Successfully resolved S3 URL for: $filename")
            response.url
        } catch (e: Exception) {
            Log.e("CoilResolver", "Failed to resolve S3 URL for: $filename", e)
            ""
        }
    }
}