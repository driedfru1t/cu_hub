package com.nikol.network

sealed interface ApiResponse<out T> {
    data class Success<out R>(val body: R) : ApiResponse<R>
    data class Error(val code: Int, val errorBody: String) : ApiResponse<Nothing>
}