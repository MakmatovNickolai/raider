package ru.raider.date.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.message_from_item.view.*
import kotlinx.android.synthetic.main.message_to_item.view.*
import ru.raider.date.R
import java.text.FieldPosition

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = intent.getStringExtra("username")

        val adapter = GroupAdapter<GroupieViewHolder>()
        val message = Message("Xer", "Metetettstt")
        adapter.add(MessageToItem(message))
        adapter.add(MessageToItem(message))
        adapter.add(MessageFromItem(message))
        adapter.add(MessageToItem(message))
        adapter.add(MessageFromItem(message))
        adapter.add(MessageToItem(message))

        chatMessagesRecycler.adapter = adapter
    }
}

class MessageFromItem(val message:Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemMessageFrom.text = message.message
        viewHolder.itemView.imageViewFrom?.setImageResource(R.drawable.emma_watson)
        //Picasso.get().load("https://img.icons8.com/material/4ac144/256/user-male.png").into(viewHolder.itemView.imageViewFrom)
    }

    override fun getLayout(): Int {
        return R.layout.message_from_item
    }
}

class MessageToItem(val message:Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemMessageTo.text = message.message
        Picasso.get().load("https://img.icons8.com/material/4ac144/256/user-male.png").into(viewHolder.itemView.imageViewTo)
    }

    override fun getLayout(): Int {
        return R.layout.message_to_item
    }
}