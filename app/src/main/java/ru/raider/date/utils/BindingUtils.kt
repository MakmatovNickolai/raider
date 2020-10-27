package ru.raider.date.utils

import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter

import com.squareup.picasso.Picasso
import sha256

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@BindingAdapter("image")
fun ImageView.loadImage(url: String?) {
    val salt = "damitrotakabratalimvvv"
    val encodedSource = Base64.encodeToString(("$url").toByteArray(), Base64.URL_SAFE)
    val str = "/auto/720/1080/sm/true/$encodedSource"

    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec("xersukkkhhrar".toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)
    val signedUrl = Base64.encodeToString(sha256Hmac.doFinal((salt + str).toByteArray()), Base64.URL_SAFE)
    val newUrl = "https://raiderimgproxy.herokuapp.com/$signedUrl$str"
    Picasso.get()
            .load(newUrl)
            .into(this)
}