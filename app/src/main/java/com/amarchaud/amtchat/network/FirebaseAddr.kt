package com.amarchaud.amtchat.network

object FirebaseAddr {
    private const val FROM = "myUid"
    private const val WITH = "userUid"
    const val UserAllMessages = "/user-messages/conversation_of_$FROM"
    fun loadAllMessagesOfUser(userUid: String): String {
        return UserAllMessages.replace(FROM, userUid)
    }

    const val UserMessagesOnePersonAddr = "/user-messages/conversation_of_$FROM/with_$WITH"
    fun loadUserMessageForOnePerso(from: String, with: String): String {
        return UserMessagesOnePersonAddr.replace(FROM, from).replace(WITH, with)
    }
}