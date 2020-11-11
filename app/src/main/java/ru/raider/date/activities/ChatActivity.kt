package ru.raider.date.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.adapter_models.MessageFromItem
import ru.raider.date.adapter_models.MessageToItem
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.network_models.Message
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network_models.User
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var apiClient: RaiderApiClient
    private lateinit var pusher: Pusher
    private var roomId: String =""
    private lateinit var toUser: User
    private var userMainPic: String =""
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        apiClient = RaiderApiClient()

        // 10.11.2020 поменять текст сообщения при переходе назад, если текст поменялся  -- можно сделать через startactivityforresult, но мне не понравилось чет
        toUser = intent.getParcelableExtra<User>("user")!!
        roomId = intent.getStringExtra("roomId")!!
        setupPusher()
        setupToolbar()
        registerEventListeners()

        chatMessagesRecycler.adapter = adapter

        loadMessages()
    }

    private fun registerEventListeners() {
        /* пока лишнее
        inputMessage.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                send()
                return@OnKeyListener true
            }
            false
        })

         */

        chatToolbarProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", toUser)
            intent.putExtra("activityType", "FromChat")
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(idChatToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            textView3.text = toUser.name
            Picasso.get().load(toUser.main_picture_url).into(idChatProfileImage)
        }
    }

    fun loadMessages() {
        apiClient.getApiService(this).fetchMessages(roomId).enqueue(object :
            Callback<List<Message>> {
            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())
            }

            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                val messages = response.body()
                messages?.forEach { m ->
                    if (m.fromUserId != toUser.id) {
                        adapter.add(MessageToItem(m))
                    } else {
                        adapter.add(MessageFromItem(m))
                    }
                }
            }
        })
    }


    private fun setupPusher() {
        val options = PusherOptions()
        options.setCluster("eu")
        pusher = Pusher("e961a899f6e47853618e", options)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i(
                    "Pusher",
                    "State changed from ${change.previousState} to ${change.currentState}"
                )
            }

            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
                Log.i(
                    "Pusher",
                    "There was a problem connecting! code ($code), message ($message), exception($e)"
                )
            }
        }, ConnectionState.ALL)


        val channel = pusher.subscribe(roomId)
        channel.bind("new_message") { event ->
            Log.i("Pusher", "Received event with data: $event")
            try {

                Log.i("Pusher", event.data)
                val m = Gson().fromJson(event.data, Message::class.java)
                this@ChatActivity.runOnUiThread(java.lang.Runnable {
                    Toast.makeText(this@ChatActivity, m.message, Toast.LENGTH_LONG).show()
                })

                if (m.fromUserId == toUser.id && m.roomId == roomId) {
                    this@ChatActivity.runOnUiThread(java.lang.Runnable {
                        adapter.add(MessageFromItem(m))
                        chatMessagesRecycler.scrollToPosition(adapter.itemCount - 1)
                    })
                }

            }
            catch (e: java.lang.Exception) {
                Log.e("DEv", e.message!!)
            }


        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun send () {
        var messageText = inputMessage.text.toString()
        if (messageText.isNullOrEmpty()) {
            return
        }
        if (pusher.connection == null) {
            return
        }

        Log.i("DEV", pusher.connection .toString())
        inputMessage.text.clear()

        messageText = messageText.trim(' ', '\t', '\n', '\r')
        val message = Message(
            id = UUID.randomUUID().toString(),
            message = messageText,
            fromUserId = toUser.id!!,
            roomId = roomId
        )

        apiClient.getApiService(this).sendMessage(pusher.connection.socketId, message).enqueue(object :
            Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {

                val simpleResponse = response.body()
                simpleResponse?.let {
                    Log.i("DEV", simpleResponse.result)
                }
                adapter.add(MessageToItem(message))
                chatMessagesRecycler.scrollToPosition(adapter.itemCount - 1)
            }
        })
    }

    fun sendMessage(view: View) {
        send()
    }

    override fun onStop() {
        // в целом пашет, надо следить за работой, и чтобы лишних подключений не было
        if (pusher.connection.state == ConnectionState.CONNECTED) {
           // pusher.unsubscribe(roomId)
            pusher.disconnect()
            Log.i("Dev", "pusher.unsubscribe(")
        }
        super.onStop()
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



