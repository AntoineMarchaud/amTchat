package com.amarchaud.amtchat.bindingadapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.amarchaud.amtchat.R
import com.bumptech.glide.Glide

object ImageViewBindingAdapterLoad {
    @JvmStatic
    @BindingAdapter("onImageLoadFromUrl")
    fun setOnImageLoadFromUrl(view: ImageView, url: String?) {
        url?.let {
            try {
                Glide.with(view)
                    .load(Uri.parse(url))
                    .into(view)
            } catch (e: IllegalArgumentException) {
                // todo
                view.setImageResource(R.drawable.common_full_open_on_phone)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("onImageLoadFromUri")
    fun setOnImageLoadFromStr(view: ImageView, uri: Uri?) {
        uri?.let {
            try {
                Glide.with(view)
                    .load(uri)
                    .into(view)
            } catch (e: IllegalArgumentException) {
                // todo
                view.setImageResource(R.drawable.common_full_open_on_phone)
            }
        }
    }
}