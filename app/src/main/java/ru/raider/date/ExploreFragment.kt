package ru.raider.date

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.android.synthetic.main.explore_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.raider.date.api.RaiderApiClient
import ru.raider.date.auth.SessionManager
import ru.raider.date.auth.SimpleResponse

class ExploreFragment : Fragment() {

    private lateinit var apiClient: RaiderApiClient
    private val adapter = ProfilesAdapter()
    private lateinit var layoutManager: CardStackLayoutManager
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.explore_fragment, container, false)

    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        layoutManager = CardStackLayoutManager(context, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }


        stack_view.layoutManager = layoutManager
        stack_view.adapter = adapter
        stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }


        apiClient = RaiderApiClient()

        apiClient.getApiService(context).getProfiles().enqueue(object : Callback<List<Profile>> {
            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                response.body()?.let {
                    adapter.setProfiles(it)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun like() {
        apiClient.getApiService(context).like("12").enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.v("DEV", "err")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                Log.v("DEV", "kras")
            }
        })
    }
}