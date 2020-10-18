package ru.raider.date

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.facebook.drawee.backends.pipeline.Fresco
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.explore_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.api.RaiderApiClient
import ru.raider.date.auth.SessionManager
import ru.raider.date.auth.SimpleResponse

class ExploreFragment : Fragment(), CardStackListener {

    private lateinit var apiClient: RaiderApiClient
    private val adapter = ProfilesAdapter()
    private lateinit var layoutManager: CardStackLayoutManager
    private lateinit var sessionManager: SessionManager


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Fresco.initialize(activity)
        val rootView:View = inflater.inflate(R.layout.explore_fragment, container, false)


        layoutManager = CardStackLayoutManager(activity, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }

        rootView.imageButton.setOnClickListener{
            val ProfileAct: Intent = Intent(it.context, ProfileActivity::class.java)
            startActivity(ProfileAct)
        }



        rootView.stack_view.layoutManager = layoutManager
        rootView.stack_view.adapter = adapter
        rootView.stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        apiClient = RaiderApiClient()

        apiClient.getApiService(activity?.applicationContext!!).getProfiles().enqueue(object : Callback<List<Profile>> {
            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                response.body()?.let {
                    adapter.setProfiles(it)
                }
            }
        })

        return rootView
    }



    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }

    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun like() {
        apiClient.getApiService(activity?.applicationContext!!).like("12").enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.v("DEV", "err")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                Log.v("DEV", "kras")
            }
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        direction?.name?.let { Log.v("DEV", it) }
        if (direction == Direction.Right) {
            like()
        }
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }
}