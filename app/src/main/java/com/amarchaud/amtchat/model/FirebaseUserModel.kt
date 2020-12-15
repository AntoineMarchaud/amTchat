package com.amarchaud.amtchat.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "users")
@Parcelize
data class FirebaseUserModel(
    @PrimaryKey @ColumnInfo(name = "uid") val uid: String = "",
    @ColumnInfo(name = "username") val username: String? = null,
    @ColumnInfo(name = "profileImageUrl") val profileImageUrl: String? = null
) : Parcelable
