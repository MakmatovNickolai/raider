package ru.raider.date.models

import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import convertImageUrl
import kotlinx.android.synthetic.main.matches_recycler_view.view.*
import ru.raider.date.R

class MatchItem(val user: User, val isBoth: Boolean): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.idMatchNameAndAge.text = user.name + ", " + user.age
        val url = user.pictureUrl
        //val newUrl = convertImageUrl(url)
        Picasso.get().load(url).into(viewHolder.itemView.idMatchAvatar)
    }

    override fun getLayout(): Int {
        return R.layout.matches_recycler_view
    }
}