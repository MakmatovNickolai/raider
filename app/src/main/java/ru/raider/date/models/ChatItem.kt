package ru.raider.date.models

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import ru.raider.date.R

class ChatItem(val roomRecord: RoomRecord): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemUsername.text = roomRecord.user?.name
        viewHolder.itemView.itemPreviewMessage.text = roomRecord.lastMessage
        Picasso.get().load(roomRecord.user?.pictureUrl).into(viewHolder.itemView.itemAvatar)
    }

    override fun getLayout(): Int {
        return R.layout.recyclerview_item_row
    }
}