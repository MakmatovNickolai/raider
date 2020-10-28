package ru.raider.date.utils

import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter

import com.squareup.picasso.Picasso
import convertImageUrl
import sha256

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@BindingAdapter("image")
fun ImageView.loadImage(url: String?) {
    val newUrl = convertImageUrl(url)
    Picasso.get()
            .load(newUrl)
            .into(this)
}