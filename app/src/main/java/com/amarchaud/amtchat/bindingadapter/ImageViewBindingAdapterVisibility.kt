package com.amarchaud.amtchat.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter

object ImageViewBindingAdapterVisibility {
    @JvmStatic
    @BindingAdapter("isVisible")
    fun setIsVisible(view: View, isVisible: Boolean) {
        if (isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}