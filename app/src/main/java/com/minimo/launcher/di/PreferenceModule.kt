package com.minimo.launcher.di

import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.data.PreferenceHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {
    @Binds
    abstract fun providePreferenceHelper(helper: PreferenceHelperImpl): PreferenceHelper
}