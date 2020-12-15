package com.amarchaud.amtchat.viewmodel

import com.amarchaud.amtchat.model.FirebaseChatMessageModel

data class ItemChatViewModel(val firebaseChatMessageModel: FirebaseChatMessageModel?,
                             val photoUrl : String?)
