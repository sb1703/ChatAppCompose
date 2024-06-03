package com.example.chatapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.chatapp.data.remote.KtorApi
import com.example.chatapp.data.repository.DataStoreOperationsImpl
import com.example.chatapp.data.repository.RepositoryImpl
import com.example.chatapp.domain.repository.DataStoreOperations
import com.example.chatapp.domain.repository.RemoteDataSource
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.Constants.PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDataStorePreferences(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreOperations(
        dataStore: DataStore<Preferences>
    ): DataStoreOperations {
        return DataStoreOperationsImpl(dataStore = dataStore)
    }

    @Provides
    @Singleton
    fun provideRepository(
        dataStoreOperations: DataStoreOperations,
        ktorApi: KtorApi,
        remote: RemoteDataSource
    ): Repository {
        return RepositoryImpl(
            dataStoreOperations = dataStoreOperations,
            ktorApi = ktorApi,
            remote = remote
        )
    }

}