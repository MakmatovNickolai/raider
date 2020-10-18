package ru.raider.date.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.squareup.picasso.Picasso

@BindingAdapter("image")
fun loadImage(view: SimpleDraweeView, url: String) {
    view.setImageURI(url)
}

@BindingAdapter(value = ["setImageUrl"])
fun ImageView.bindImageUrl(url: String?) {
    if (url != null && url.isNotBlank()) {

        Picasso.get()
            .load(url)
            .into(this)
    }
}