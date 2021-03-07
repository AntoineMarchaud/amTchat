package com.amarchaud.amtchat.model.app

import com.amarchaud.amtchat.model.FirebaseChatMessageModel

data class ItemChatViewModel(val firebaseChatMessageModel: FirebaseChatMessageModel?,
                             val photoUrl : String?)
