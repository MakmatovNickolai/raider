package ru.raider.date.models

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.matches_recycler_view.view.*
import ru.raider.date.R

class MatchItem(val matchProfile: Profile): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //viewHolder.itemView.itemUsername.text = roomRecord.user.name
        // viewHolder.itemView.itemPreviewMessage.text = roomRecord.lastMessage.message
        Picasso.get().load(matchProfile.pictureUrl).into(viewHolder.itemView.matchAvatar)
    }

    override fun getLayout(): Int {
        return R.layout.matches_recycler_view
    }
}