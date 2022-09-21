package com.sugadev.noteit.base.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.local.NoteDatabase
import com.sugadev.noteit.local.NoteRepositoryImpl
import com.sugadev.noteit.local.model.NoteDao
import com.sugadev.noteit.local.preference.userPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        NoteRepositoryImpl(noteDao = noteDao)

    @Singleton
    @Provides
    fun provideFirebaseAnalytic(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userPreferencesDataStore
    }

}