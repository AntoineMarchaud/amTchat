package com.amarchaud.amtchat.model.app

import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel

data class ItemLastMessageViewModel(
    val lastConvUser: FirebaseUserModel,
    var lastConvChat: FirebaseChatMessageModel
)
