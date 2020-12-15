package com.amarchaud.amtchat.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import io.reactivex.rxjava3.core.Observable

@Dao
interface AmTchatDao {

    /**
     * User Table
     */
    @Insert
    fun insertUser(user: FirebaseUserModel)

    @Query("SELECT * from users ORDER BY username ASC")
    fun getAllUsers(): List<FirebaseUserModel>

    @Query("SELECT * from users ORDER BY username ASC")
    fun getAllUsersRx(): Observable<List<FirebaseUserModel>>

    @Query("SELECT * from users WHERE uid=:uid")
    fun getMySelf(uid : String) : FirebaseUserModel

    @Delete
    fun deleteUser(user : FirebaseChatMessageModel)

    /**
     * Message Table
     */

    @Insert
    fun insertChatMessage(chatMessageModel: FirebaseChatMessageModel)

    @Query("SELECT * from messages ORDER BY timestamp ASC")
    fun getMessages(): List<FirebaseChatMessageModel>

    @Delete
    fun DeleteMessage(chatMessageModel: FirebaseChatMessageModel)

    //@Query("SELECT * from messages WHERE fromId=:uid")
    //fun GetLastMessageOfUser(uid : String) : List<FirebaseChatMessageModel>
}