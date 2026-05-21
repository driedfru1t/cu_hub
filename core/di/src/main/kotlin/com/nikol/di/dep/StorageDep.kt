package com.nikol.di.dep

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nikol.storage.di.qualifers.TokenDataStore

interface StorageDep: DataStoreTokenDep

interface DataStoreTokenDep {
    @TokenDataStore
    fun tokenDataStore(): DataStore<Preferences>
}