package com.amarchaud.amtchat.network

object FirebaseAddr {
    private const val FROM = "myUid"
    private const val WITH = "userUid"
    const val UserMessageAddr = "/user-messages/conversation_of_$FROM/with_$WITH"
    fun loadUserMessage(from: String, with: String): String {
        return UserMessageAddr.replace(FROM, from).replace(WITH, with)
    }
}