package com.minimo.launcher.di

import android.app.Application
import androidx.room.Room
import com.minimo.launcher.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDb(application: Application) =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java, "minimo-launcher-db"
        ).build()

    @Provides
    fun providesAppInfoDao(db: AppDatabase) = db.appInfoDao()
}