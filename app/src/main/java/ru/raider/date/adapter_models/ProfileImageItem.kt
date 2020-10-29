package ru.raider.date.adapter_models

import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import convertImageUrl
import kotlinx.android.synthetic.main.fragment_profile_edit_photos.view.*
import kotlinx.android.synthetic.main.matches_recycler_view.view.*
import kotlinx.android.synthetic.main.profile_edit_photo_recycler_item.view.*
import ru.raider.date.R
import ru.raider.date.network_models.User

class ProfileImageItem(val url: String?, val bitmap: Bitmap?): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (url.isNullOrEmpty()) {
            if (bitmap != null) {
                viewHolder.itemView.idProfileEditPhotoImageView.setImageBitmap(bitmap)
            } else {
                viewHolder.itemView.idProfileEditPhotoImageView.setImageResource(R.drawable.ic_baseline_add_24)
            }
        } else {
            val newUrl = convertImageUrl(url)
            Picasso.get().load(newUrl).into(viewHolder.itemView.idProfileEditPhotoImageView)
        }

    }
    override fun getLayout(): Int {
        return R.layout.profile_edit_photo_recycler_item
    }
}