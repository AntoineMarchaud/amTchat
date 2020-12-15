package com.amarchaud.amtchat.viewmodel

import com.amarchaud.amtchat.model.FirebaseUserModel

data class ItemChooseUserViewModel(val me: FirebaseUserModel, val other : FirebaseUserModel)