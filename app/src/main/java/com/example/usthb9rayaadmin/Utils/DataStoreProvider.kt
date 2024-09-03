package com.example.usthb9rayaadmin.Utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences


object DataStoreProvider {
    private lateinit var dataStore: DataStore<Preferences>

    fun getInstance(context: Context): DataStore<Preferences> {
        if (!::dataStore.isInitialized) {
            dataStore = PreferenceDataStoreFactory.create(
                produceFile = { context.filesDir.resolve("isFileDownloaded.preferences_pb") }
            )}
        return dataStore
    }
}