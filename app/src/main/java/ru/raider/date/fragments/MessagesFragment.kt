package ru.raider.date.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.messages_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.network_models.FetchRoomsResponse
import ru.raider.date.activities.ChatActivity
import ru.raider.date.activities.MainActivity
import ru.raider.date.adapter_models.ChatItem

class MessagesFragment : Fragment() {
    private var adapter = GroupAdapter<GroupieViewHolder>()
    private var needLoadUsers = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.messages_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            if (item is ChatItem) {
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.putExtra("username", item.roomRecord.user?.name)
                intent.putExtra("toUserId",  item.roomRecord.user?.id)
                intent.putExtra("roomId",  item.roomRecord.room?.id)
                startActivity(intent)
            }
        }

        if (needLoadUsers) {
            val mainActivity = activity as MainActivity
            // TODO: 30.10.2020 Добавить анимацию загрузки во всех местах где есть загрузка 
            mainActivity.apiClient.getApiService(mainActivity).fetchRooms().enqueue(object : Callback<FetchRoomsResponse> {
                override fun onFailure(call: Call<FetchRoomsResponse>, t: Throwable) {
                    Log.i("DEV", call.toString())
                    Log.i("DEV", t.message.toString())

                }

                override fun onResponse(call: Call<FetchRoomsResponse>, response: Response<FetchRoomsResponse>) {
                    val fetchRoomsResponse = response.body()
                    fetchRoomsResponse?.let {
                        for (roomRecord in it.result) {
                            adapter.add(ChatItem(roomRecord))
                        }
                        needLoadUsers = false
                    }
                }
            })
        }
    }

    companion object {
        fun newInstance(): MessagesFragment = MessagesFragment()
    }
}

