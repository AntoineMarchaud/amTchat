package com.amarchaud.amtchat.injection.module

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContentResolverModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContentResolver(): ContentResolver {
        return context.contentResolver
    }
}