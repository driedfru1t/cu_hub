package com.nikol.lms.backround

import arrow.core.raise.context.Raise
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareGet
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.OutputStream
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http

data class DownloadParam(
    val filename: String,
    val version: String?
)

@Serializable
data class UrlResponse(
    val url: String
)

class DownloadCuResService @Inject constructor(
    @param:Http(CuHttpClient.CU) private val cuClient: HttpClient,
    @param:Http(CuHttpClient.Coil) private val resClient: HttpClient,
    json: Json
) : BaseRemoteDataSource(json) {

    context(raise: Raise<NetworkError>)
    suspend fun getLink(param: DownloadParam): UrlResponse = raise.safeApiCall {
        cuClient.get("content/download-link") {
            parameter("filename", param.filename)
            parameter("version", param.version)
        }
    }

    suspend fun downloadFile(
        url: String,
        createOutputStream: (mimeType: String) -> OutputStream,
        onProgress: (Int) -> Unit
    ) {
        var lastUpdateTime = 0L
        var lastProgress = -1

        resClient.prepareGet(url) {
            onDownload { bytesDownloaded, totalBytes ->
                if (totalBytes != null && totalBytes > 0) {
                    val progress = ((bytesDownloaded * 100) / totalBytes).toInt()
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - lastUpdateTime > 300 && progress != lastProgress) {
                        lastUpdateTime = currentTime
                        lastProgress = progress
                        onProgress(progress)
                    }
                }
            }
        }.execute { response ->
            val rawContentType = response.headers["Content-Type"]
            val mimeType = rawContentType?.substringBefore(';') ?: "application/octet-stream"

            val outputStream = createOutputStream(mimeType)

            val channel: ByteReadChannel = response.body()
            outputStream.use { output ->
                val buffer = ByteArray(16 * 1024)
                while (!channel.isClosedForRead) {
                    val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                    if (bytesRead <= 0) break
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
    }
}