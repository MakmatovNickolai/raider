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
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.models.FetchRoomsResponse
import ru.raider.date.activities.ChatActivity
import ru.raider.date.models.ChatItem

class MessagesFragment : Fragment() {
    private lateinit var apiClient: RaiderApiClient
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.messages_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = GroupAdapter<GroupieViewHolder>()

        apiClient = RaiderApiClient()
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            if (item is ChatItem) {
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.putExtra("username", item.roomRecord.user?.name)
                intent.putExtra("to_user_id",  item.roomRecord.user?.id)
                intent.putExtra("room_id",  item.roomRecord.room?.id)
                startActivity(intent)
            }
        }

        apiClient.getApiService(activity?.applicationContext!!).fetchRooms().enqueue(object : Callback<FetchRoomsResponse> {
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
                }
            }
        })
    }


    companion object {
        fun newInstance(): MessagesFragment = MessagesFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}

