package com.minimo.launcher.di

import android.app.Application
import androidx.room.Room
import com.minimo.launcher.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDb(application: Application) =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java, "minimo-launcher-db"
        ).build()

    @Singleton
    @Provides
    fun providesAppInfoDao(db: AppDatabase) = db.appInfoDao()
}