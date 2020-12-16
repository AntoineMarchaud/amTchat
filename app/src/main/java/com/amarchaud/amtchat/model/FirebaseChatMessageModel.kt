package com.amarchaud.amtchat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class FirebaseChatMessageModel(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = "", // id du message
    @ColumnInfo(name = "text") var text: String? = null,
    @ColumnInfo(name = "fromId") val fromId: String = "", // id de lenvoyeur
    @ColumnInfo(name = "toId") val toId: String = "", // id du receveur
    @ColumnInfo(name = "timestamp") val timestamp: Long = 0,
    @ColumnInfo(name = "isDeleted") var isDeleted : Boolean = false,
    var isSent : Boolean = false,
    var isReceived : Boolean = false,
    var isRead : Boolean = false
)