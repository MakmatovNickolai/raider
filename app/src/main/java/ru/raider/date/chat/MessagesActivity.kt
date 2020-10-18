package ru.raider.date.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import ru.raider.date.R


class MessagesActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val chat = Chat("Аяз", "хер шухра хочешь?y", "https://www.gravatar.com/avatar/9f5e6da3e999b8d2511748618e326e40?s=328&d=identicon&r=PG")
        val chat1 = Chat("Драл", "ИОжет в амог ассс?y", "https://www.gravatar.com/avatar/9f5e6da3e999b8d2511748618e326e40?s=328&d=identicon&r=PG")
        val chat2 = Chat("Аяз", "хер шухра хочешь?y", "https://www.gravatar.com/avatar/9f5e6da3e999b8d2511748618e326e40?s=328&d=identicon&r=PG")
        val chatList = listOf(chat, chat1, chat2)

        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatItem(chat))
        adapter.add(ChatItem(chat1))
        adapter.add(ChatItem(chat2))
        adapter.setOnItemClickListener { item, view ->
            val chatItem = item as ChatItem
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("username", chatItem.chat.username)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}

class ChatItem(val chat: Chat): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemUsername.text = chat.username
        viewHolder.itemView.itemPreviewMessage.text = chat.previewMessage
        Picasso.get().load(chat.avatar_link).into(viewHolder.itemView.itemAvatar)
    }

    override fun getLayout(): Int {
        return R.layout.recyclerview_item_row
    }
}