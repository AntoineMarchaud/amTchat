package com.amarchaud.amtchat.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel

@Database(entities = [FirebaseUserModel::class, FirebaseChatMessageModel::class], version = 1, exportSchema = false)
abstract class AmTchatDb : RoomDatabase() {
    abstract fun amTchatDao(): AmTchatDao
}