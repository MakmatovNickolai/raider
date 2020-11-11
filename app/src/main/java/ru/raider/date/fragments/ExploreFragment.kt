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
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_matches.*
import kotlinx.android.synthetic.main.fragment_messages.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.R
import ru.raider.date.activities.MainActivity
import ru.raider.date.adapter_models.ExploreUserItem
import ru.raider.date.network_models.FetchUserResponse
import ru.raider.date.network_models.SimpleResponse
import ru.raider.date.network_models.NoConnectivityException
import java.net.ConnectException

class ExploreFragment : Fragment(), CardStackListener {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var layoutManager: CardStackLayoutManager
    private var haveAnyLoadedUsers = false

    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            loadUsers()
        } else {
            Log.i("DEV", "ExploreFragment savedInstanceState != null")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


    private fun loadUsers() {
        if (haveAnyLoadedUsers) {
            idExploreLoadingProgressBar.visibility = View.GONE
        } else {
            idExploreLoadingProgressBar.visibility = View.VISIBLE
            val mainActivity = activity as MainActivity
            mainActivity.apiClient.getApiService(mainActivity).fetchUsers().enqueue(object :
                    Callback<FetchUserResponse> {
                override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                    if (!this@ExploreFragment.isVisible) {
                        return
                    }
                    idExploreLoadingProgressBar.visibility = View.GONE

                    //  надо чтобы возвращался FetchUserResponse, переделать все ответы под один тип


                    if (t is NoConnectivityException || t is ConnectException) {
                        idNoExploreProfilesTextView.text = "NoConnection"
                    } else {
                        idNoExploreProfilesTextView.text = t.message.toString()
                    }
                    idNoExploreProfilesTextView.visibility = View.VISIBLE
                }

                override fun onResponse(
                        call: Call<FetchUserResponse>,
                        response: Response<FetchUserResponse>
                ) {
                    if (!this@ExploreFragment.isVisible) {
                        return
                    }
                    idExploreLoadingProgressBar.visibility = View.GONE
                    val fetchUserResponse = response.body()
                    if (fetchUserResponse != null)
                    {
                        if (fetchUserResponse.result.size > 0) {
                            haveAnyLoadedUsers = true
                            for (user in fetchUserResponse.result) {
                                adapter.add(ExploreUserItem(user))
                            }
                        } else {
                            idNoExploreProfilesTextView.visibility = View.VISIBLE
                        }
                    } else {
                        idNoExploreProfilesTextView.text = "no response"
                        idNoExploreProfilesTextView.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}


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

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}
}