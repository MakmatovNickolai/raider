package ru.raider.date.adapter_models

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_from_item.view.*
import ru.raider.date.R
import ru.raider.date.network_models.Message

class MessageFromItem(val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemMessageFrom.text = message.message
        //Picasso.get().load("https://img.icons8.com/material/4ac144/256/user-male.png").into(viewHolder.itemView.imageViewFrom)
    }

    override fun getLayout(): Int {
        return R.layout.message_from_item
    }
}