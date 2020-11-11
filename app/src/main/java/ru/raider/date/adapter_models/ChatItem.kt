package ru.raider.date.adapter_models

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import ru.raider.date.R
import ru.raider.date.network_models.RoomRecord

class ChatItem(val roomRecord: RoomRecord): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemUsername.text = roomRecord.user?.name
        val preview = if (roomRecord.lastMessage.isNullOrEmpty()) " \uD83D\uDE0A Новая беседа, напиши что-нибудь!" else roomRecord.lastMessage
        viewHolder.itemView.itemPreviewMessage.text = preview
        Picasso.get().load(roomRecord.user?.main_picture_url).into(viewHolder.itemView.itemAvatar)
    }

    override fun getLayout(): Int {
        return R.layout.recyclerview_item_row
    }
}