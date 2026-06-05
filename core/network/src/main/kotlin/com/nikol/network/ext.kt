package com.nikol.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.toApiResponse(): ApiResponse<T> {
    return if (status.isSuccess()) {
        ApiResponse.Success(body<T>())
    } else {
        ApiResponse.Error(status.value, bodyAsText())
    }
}