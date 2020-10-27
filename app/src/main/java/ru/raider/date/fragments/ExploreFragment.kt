package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.explore_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.models.Profile
import ru.raider.date.R
import ru.raider.date.adapters.ProfilesAdapter
import ru.raider.date.network.RaiderApiClient
import ru.raider.date.models.FetchUserResponse
import ru.raider.date.models.SimpleResponse

class ExploreFragment : Fragment(), CardStackListener {
    private var profiless: MutableList<Profile>? = null
    private lateinit var apiClient: RaiderApiClient
    private val adapter = ProfilesAdapter()

    private lateinit var layoutManager: CardStackLayoutManager

    private var isFirst = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView:View = inflater.inflate(R.layout.explore_fragment, container, false)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            layoutManager = CardStackLayoutManager(activity, this).apply {
                setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
                setOverlayInterpolator(LinearInterpolator())
                setCanScrollVertical(false)
            }

            stack_view.layoutManager = layoutManager
            stack_view.adapter = adapter
            stack_view.itemAnimator.apply {
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
            //stack_view.isNestedScrollingEnabled = false;
            if (isFirst) {
                apiClient = RaiderApiClient()
                profiless = mutableListOf()
                apiClient.getApiService(activity?.applicationContext!!).fetchUsers().enqueue(object : Callback<FetchUserResponse> {
                    override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                        Log.i("DEV", call.toString())
                        Log.i("DEV", t.message.toString())

                    }

                    override fun onResponse(call: Call<FetchUserResponse>, response: Response<FetchUserResponse>) {
                        val fetchUserResponse = response.body()
                        fetchUserResponse?.let {
                            val copy:MutableList<Profile> = mutableListOf()
                            for (name in it.result) {
                                copy.add(name)
                            }
                            profiless = copy
                            isFirst = false
                            adapter.setsProfiles(it.result)
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
            } else {
                val copy:MutableList<Profile> = mutableListOf()
                for (name in profiless!!) {
                   copy.add(name)
                }
                adapter.setsProfiles(copy)
                adapter.notifyDataSetChanged()
            }
        } else {
            Log.i("DEV", "savedstate exists")
        }

    }
    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun like(id: String, direction: Int?) {
        val profile = profiless?.removeAt(0)
        Log.v("DEV", profile?.name.toString())
        apiClient.getApiService(activity?.applicationContext!!).like(id, direction.toString()).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.v("DEV", "err")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {


            }
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        val profile = profiless?.get(0)
        profile?.id?.let { like(it.toString(), direction?.ordinal) }
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }
}