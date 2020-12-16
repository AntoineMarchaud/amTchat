package com.amarchaud.amtchat.viewmodel

import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel

data class ItemLastMessageViewModel(
    val lastConvUser: FirebaseUserModel,
    var lastConvChat: FirebaseChatMessageModel
)
