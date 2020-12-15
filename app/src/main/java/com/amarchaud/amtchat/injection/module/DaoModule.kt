package com.amarchaud.amtchat.injection.module

import android.app.Application
import androidx.room.Room
import com.amarchaud.amtchat.model.database.AmTchatDao
import com.amarchaud.amtchat.model.database.AmTchatDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideDatabase(): AmTchatDb {
        return Room.databaseBuilder(
            application,
            AmTchatDb::class.java,
            "amTchat_db"
        ).build()
    }


    @Singleton
    @Provides
    fun provideDao(amTchatDb: AmTchatDb): AmTchatDao {
        return amTchatDb.amTchatDao()
    }
}