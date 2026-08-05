package com.nikol.sync

fun interface Syncable {
    suspend fun sync(): SyncResult
}