package ru.raider.date.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.explore_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.activities.MainActivity
import ru.raider.date.adapter_models.ExploreUserItem
import ru.raider.date.network_models.FetchUserResponse
import ru.raider.date.network_models.SimpleResponse

class ExploreFragment : Fragment(), CardStackListener {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var layoutManager: CardStackLayoutManager
    private var needLoadUsers = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.explore_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            // TODO: 30.10.2020  Добавить скроллинг инфо вниз при нажатии на карточку
            // TODO: 30.10.2020 Добавить город и местоположение 
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
            if (needLoadUsers) {
                Log.i("Dev", "First")
                val mainActivity = activity as MainActivity
                mainActivity.apiClient.getApiService(mainActivity).fetchUsers().enqueue(object : Callback<FetchUserResponse> {
                    override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                        Log.i("DEV", call.toString())
                        Log.i("DEV", t.message.toString())

                    }

                    override fun onResponse(call: Call<FetchUserResponse>, response: Response<FetchUserResponse>) {
                        val fetchUserResponse = response.body()
                        fetchUserResponse?.let {
                            for (user in it.result) {
                                adapter.add(ExploreUserItem(user))
                            }
                            needLoadUsers = false
                        }
                    }
                })
            } else {
                Log.i("Dev", "not First")
            }
        } else {
            Log.i("DEV", "savedstate exists")
        }

    }
    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        val user = adapter.getItem(0) as ExploreUserItem
        adapter.remove(user)
        val mainActivity = activity as MainActivity
        mainActivity.apiClient.getApiService(mainActivity).like(user.user.id!!, direction?.ordinal.toString()).enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.v("DEV", "err")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                Log.i("Dev", "liked")
            }
        })
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