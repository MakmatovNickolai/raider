package ru.raider.date.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_matches.*
import kotlinx.android.synthetic.main.fragment_messages.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.activities.ChatActivity
import ru.raider.date.activities.MainActivity
import ru.raider.date.adapter_models.ChatItem
import ru.raider.date.network_models.FetchRoomsResponse


class MessagesFragment : Fragment() {
    private var adapter = GroupAdapter<GroupieViewHolder>()
    private var haveAnyLoadedUsers = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { item, view ->
                if (item is ChatItem) {
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra("user", item.roomRecord.user)
                    intent.putExtra("roomId", item.roomRecord.room?.id)
                    startActivity(intent)
                }
            }

            swipeMessagesContainer.setOnRefreshListener {
                loadMessages(0)
            }

            if (haveAnyLoadedUsers) {
                idMessagesLoadingProgressBar.visibility = View.GONE
            }
            else {
                loadMessages(0)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun loadMessages(page:Int) {
        val mainActivity = activity as MainActivity
        if (!swipeMessagesContainer.isRefreshing) {
            idMessagesLoadingProgressBar.visibility = View.VISIBLE
        }
        idNoMessagesTextView.visibility = View.GONE
        mainActivity.apiClient.getApiService(mainActivity).fetchRooms().enqueue(object :
            Callback<FetchRoomsResponse> {
            override fun onFailure(call: Call<FetchRoomsResponse>, t: Throwable) {
                if (!this@MessagesFragment.isVisible) {
                    return
                }
                swipeMessagesContainer.isRefreshing = false
                idMessagesLoadingProgressBar.visibility = View.GONE
                idNoMessagesTextView.text = t.message.toString()
                idNoMessagesTextView.visibility = View.VISIBLE
            }

            override fun onResponse(
                call: Call<FetchRoomsResponse>,
                response: Response<FetchRoomsResponse>
            ) {
                if (!this@MessagesFragment.isVisible) {
                    return
                }
                swipeMessagesContainer.isRefreshing = false
                idMessagesLoadingProgressBar.visibility = View.GONE
                val fetchRoomsResponse = response.body()


                if (fetchRoomsResponse != null) {
                    if (fetchRoomsResponse.result.isNotEmpty()) {
                        adapter.clear()
                        for (roomRecord in fetchRoomsResponse.result) {
                            adapter.add(ChatItem(roomRecord))
                        }
                        haveAnyLoadedUsers = true
                    } else {
                        idNoMessagesTextView.text = "Нет открытых комнат, продолжай лайкать"
                        idNoMessagesTextView.visibility = View.VISIBLE
                    }
                } else {
                    idNoMessagesTextView.text = "No response"
                    idNoMessagesTextView.visibility = View.VISIBLE
                }

            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.i("DEV", "onStop")
        if (swipeMessagesContainer != null && swipeMessagesContainer.isRefreshing)
            swipeMessagesContainer.isRefreshing = false
    }

    companion object {
        fun newInstance(): MessagesFragment = MessagesFragment()
    }
}

