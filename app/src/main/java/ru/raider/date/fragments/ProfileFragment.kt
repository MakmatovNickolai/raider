package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.test_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.models.User
import ru.raider.date.R
import ru.raider.date.activities.MainActivity
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.models.FetchUserResponse
import ru.raider.date.models.MatchItem
import ru.raider.date.models.SimpleResponse


class TestFragment : Fragment() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private var needLoadUsers = true
    private var isBoth = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.test_fragment, container, false)

    companion object {
        fun newInstance(): TestFragment = TestFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.setOnItemClickListener { item, view ->
            if (item is MatchItem) {
                val mainActivity = activity as MainActivity
                if (item.isBoth) {
                    mainActivity.apiClient.getApiService(mainActivity).createRoom(item.user.id).enqueue(object : Callback<SimpleResponse> {
                        override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                            Log.i("DEV", call.toString())
                            Log.i("DEV", t.message.toString())
                        }

                        override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                            val simpleResponse = response.body()
                            Log.i("DEV", simpleResponse.toString())
                        }
                    })
                } else {
                    mainActivity.apiClient.getApiService(mainActivity).like(item.user.id, "1").enqueue(object : Callback<SimpleResponse> {
                        override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                            Log.i("DEV", call.toString())
                            Log.i("DEV", t.message.toString())

                        }

                        override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                            val simpleResponse = response.body()
                            Log.i("DEV", simpleResponse.toString())
                        }
                    })
                }

            }
        }
        matchesRecyclerView.adapter = adapter
        if (needLoadUsers) {
            getMatches("both")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.fragment_matches_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mutual_likes -> {
                adapter.clear()
                isBoth = true
                getMatches("both")
            }
            R.id.liked_me -> {
                adapter.clear()
                isBoth = false
                getMatches("one")
            }
            else -> {
                Log.i("DEV", item.itemId.toString())
            }
        }
        return true
    }

    private fun getMatches(type:String) {
        val mainActivity = activity as MainActivity
        mainActivity.apiClient.getApiService(mainActivity).getMatches(type).enqueue(object : Callback<FetchUserResponse> {
            override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())
            }

            override fun onResponse(call: Call<FetchUserResponse>, response: Response<FetchUserResponse>) {
                val fetchUserResponse = response.body()
                fetchUserResponse?.let {
                    for (user in it.result) {
                        adapter.add(MatchItem(user, isBoth))
                    }
                }
                needLoadUsers = false
            }
        })
    }
}
