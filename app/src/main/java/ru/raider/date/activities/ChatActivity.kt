package ru.raider.date.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.models.Message
import ru.raider.date.models.MessageFromItem
import ru.raider.date.models.MessageToItem
import ru.raider.date.models.SimpleResponse
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var apiClient: RaiderApiClient
    private var toUserId: String =""
    private var roomId: String =""
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = intent.getStringExtra("username")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toUserId = intent.getStringExtra("toUserId")!!
        roomId = intent.getStringExtra("roomId")!!
        chatMessagesRecycler.adapter = adapter
        apiClient = RaiderApiClient()
        apiClient.getApiService(this).fetchMessages(roomId).enqueue(object : Callback<List<Message>> {
            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                val messages = response.body()
                messages?.forEach { m ->
                    if (m.fromUserId != toUserId) {
                        adapter.add(MessageToItem(m))
                    } else {
                        adapter.add(MessageFromItem(m))
                    }
                }

            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun sendMessage(view: View) {

        val messageText = inputMessage.text.toString()
        val message = Message(id = UUID.randomUUID().toString(), message = messageText, fromUserId = toUserId, roomId = roomId)
        inputMessage.text.clear()
        adapter.add(MessageToItem(message))
        apiClient.getApiService(this).sendMessage(message).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                val simpleResponse = response.body()
                simpleResponse?.let {
                    Log.i("DEV", simpleResponse.result)
                }
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.i("Dev", "popping backstack")
            supportFragmentManager.popBackStack()
        } else {
            Log.i("Dev", "nothing on backstack, calling super")
            super.onBackPressed()
        }
    }
}



