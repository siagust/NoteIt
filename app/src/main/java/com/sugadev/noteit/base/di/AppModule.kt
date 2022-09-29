package com.sugadev.noteit.base.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.AnalyticsManagerImpl
import com.sugadev.noteit.base.analytics.firebase.FirebaseAnalyticsClient
import com.sugadev.noteit.base.config.RemoteConfig
import com.sugadev.noteit.base.config.RemoteConfigImpl
import com.sugadev.noteit.base.local.NoteDao
import com.sugadev.noteit.base.local.NoteDatabase
import com.sugadev.noteit.base.local.NoteRepository
import com.sugadev.noteit.base.local.NoteRepositoryImpl
import com.sugadev.noteit.base.preference.userPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideNoteDao(@ApplicationContext context: Context): NoteDao {
        return NoteDatabase.getInstance(context)
            .noteDao()
    }

    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository =
        NoteRepositoryImpl(noteDao = noteDao, dispatcher = provideDispatcherIo())

    @Provides
    fun provideFirebaseAnalytic(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    @Named(DiName.DISPATCHER_IO)
    fun provideDispatcherIo(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @Provides
    fun provideAnalyticsManager(): AnalyticsManager {
        return AnalyticsManagerImpl(FirebaseAnalyticsClient(provideFirebaseAnalytic()))
    }

    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userPreferencesDataStore
    }

    @Singleton
    @Provides
    fun provideRemoteConfig(): RemoteConfig {
        return RemoteConfigImpl(FirebaseRemoteConfig.getInstance(), gson = provideGson())
    }

    @Provides
    fun provideGson(): Gson = Gson()

}