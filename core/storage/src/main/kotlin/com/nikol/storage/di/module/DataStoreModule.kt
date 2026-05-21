package com.nikol.storage.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nikol.storage.di.qualifers.TokenDataStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DataStoreModule {

    @Provides
    @Singleton
    @TokenDataStore
    fun provideAuthDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("token_prefs") }
        )
    }
}