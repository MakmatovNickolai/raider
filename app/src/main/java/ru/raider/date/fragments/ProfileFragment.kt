package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.matches_recycler_view.view.*
import kotlinx.android.synthetic.main.test_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.models.Profile
import ru.raider.date.R
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.models.FetchUserResponse
import ru.raider.date.models.MatchItem
import ru.raider.date.models.SimpleResponse


class TestFragment : Fragment() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var apiClient: RaiderApiClient
    private var profiless: MutableList<Profile>? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.test_fragment, container, false)

    companion object {
        fun newInstance(): TestFragment = TestFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        apiClient = RaiderApiClient()
        adapter.setOnItemClickListener { item, view ->
            if (item is MatchItem) {
                apiClient.getApiService(activity?.applicationContext!!).createRoom(item.matchProfile.id).enqueue(object : Callback<SimpleResponse> {
                    override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                        Log.i("DEV", call.toString())
                        Log.i("DEV", t.message.toString())

                    }

                    override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                        val fetchUserResponse = response.body()
                        Log.i("DEV", fetchUserResponse.toString())
                    }
                })
            }
        }
        matchesRecyclerView.adapter = adapter
        getMatches("both")

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

    fun getMatches(type:String) {
        apiClient.getApiService(activity?.applicationContext!!).getMatches(type).enqueue(object : Callback<FetchUserResponse> {
            override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                Log.i("DEV", call.toString())
                Log.i("DEV", t.message.toString())

            }

            override fun onResponse(call: Call<FetchUserResponse>, response: Response<FetchUserResponse>) {
                val fetchUserResponse = response.body()
                fetchUserResponse?.let {
                    for (name in it.result) {
                        adapter.add(MatchItem(name))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mutual_likes -> {
                adapter.clear()
                getMatches("both")
            }
            R.id.liked_me -> {
                adapter.clear()
                getMatches("one")
            }
            else -> {
                Log.i("DEV", item.itemId.toString())
            }
        }
        return true
    }
}
