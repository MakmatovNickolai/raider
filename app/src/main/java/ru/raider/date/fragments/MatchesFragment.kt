package ru.raider.date.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
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
import ru.raider.date.activities.ProfileActivity
import ru.raider.date.adapter_models.MatchItem
import ru.raider.date.network_models.FetchUserResponse
import ru.raider.date.network_models.User


class MatchesFragment : Fragment() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private var isBoth = false
    private var type = "one"
    private lateinit var clickedMatch:MatchItem

    private lateinit var bothUsers:MutableList<MatchItem>
    private lateinit var oneUsers:MutableList<MatchItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("DEV", "onCreateView")
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }


    companion object {
        fun newInstance(): MatchesFragment = MatchesFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("DEV", "onViewCreated")


    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("DEV", "onActivityCreated")
        if (savedInstanceState == null) {
            Log.i("DEV", "savedInstanceState == null")
            adapter.setOnItemClickListener { item, view ->
                if (item is MatchItem) {
                    onItemClicked(item)
                }
            }
            matchesRecyclerView.adapter = adapter

            swipeMatchesContainer.setOnRefreshListener {
                load(0)
            }
            configureTabLayout()
            selectPage(1)

        } else {
            Log.i("DEV", "savedInstanceState != null")
        }

    }
    fun selectPage(pageIndex: Int) {
        tabLayout.setScrollPosition(pageIndex, 0f, true)
        tabLayout.getTabAt(pageIndex)?.select()
        //viewPager.setCurrentItem(pageIndex)
    }

    private fun onItemClicked(item: MatchItem) {
        clickedMatch = item
        val mainActivity = activity as MainActivity
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.putExtra("user", item.user)
        if (item.isBoth) {
            intent.putExtra("activityType", "LikedBoth")
        } else {
            intent.putExtra("activityType", "LikedMe")
        }

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("DEV", "onActivityResult")
        Log.i("DEV", resultCode.toString())
        if (resultCode == Activity.RESULT_OK) {
            val user = data?.getParcelableExtra<User>("user")
            Log.i("DEV", user.toString())
            if (user != null) {
                adapter.remove(clickedMatch)
                if (isBoth) {
                    bothUsers.remove(clickedMatch)
                    val roomId = data.getStringExtra("roomId")
                    val intent = Intent(activity?.applicationContext!!, ChatActivity::class.java)
                    intent.putExtra("user", user)
                    intent.putExtra("roomId", roomId)
                    startActivity(intent)
                } else {
                    oneUsers.remove(clickedMatch)
                }
            }
        }
    }

    fun emptyRecyclerView() {
        Log.i("DEV", "emptyRecyclerView")
        idNoMatchesTextView.visibility = View.GONE
        idMatchesLoadingProgressBar.visibility = View.GONE
        adapter.clear()
        swipeMatchesContainer.isRefreshing = false
    }


    // табы в одном фрагменте сделал, надо по сути разделять на отдельный фрагмент каждый таб
    private fun configureTabLayout() {
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (this@MatchesFragment.isVisible) {
                    emptyRecyclerView()
                    when (tab.position) {
                        0 -> {
                            isBoth = false
                            loadMatches(0)
                        }
                        1 -> {
                            isBoth = true
                            loadMatches(0)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })
    }

    private fun load(page: Int) {
        idNoMatchesTextView.visibility = View.GONE
        if (!swipeMatchesContainer.isRefreshing) {
            idMatchesLoadingProgressBar.visibility = View.VISIBLE
        }
        val mainActivity = activity as MainActivity
        var type = "one"
        if (isBoth) {
            type ="both"
        }

        mainActivity.apiClient.getApiService(mainActivity).getMatches(type).enqueue(object :
            Callback<FetchUserResponse> {
            override fun onFailure(call: Call<FetchUserResponse>, t: Throwable) {
                if (!this@MatchesFragment.isVisible) {
                    return
                }
                swipeMatchesContainer.isRefreshing = false
                idMatchesLoadingProgressBar.visibility = View.GONE
                idNoMatchesTextView.text = t.message.toString()
                idNoMatchesTextView.visibility = View.VISIBLE
            }

            override fun onResponse(
                call: Call<FetchUserResponse>,
                response: Response<FetchUserResponse>
            ) {
                if (!this@MatchesFragment.isVisible) {
                    return
                }

                // если сменили таб, то не надо отображать результат
                if ((isBoth && type != "both") || (!isBoth && type != "one")) {
                    return
                }
                swipeMatchesContainer.isRefreshing = false
                idMatchesLoadingProgressBar.visibility = View.GONE
                val fetchUserResponse = response.body()
                if (fetchUserResponse != null) {
                    if (fetchUserResponse.result.size > 0) {
//                        anyMatchesLoaded = true
                        var tempList = mutableListOf<MatchItem>()
                        for (user in fetchUserResponse.result) {
                            tempList.add(MatchItem(user, isBoth))
                        }
                        adapter.clear()
                        adapter.addAll(tempList)
                        if (isBoth) {
                            bothUsers = mutableListOf()
                            bothUsers.addAll(tempList)
                        } else {
                            oneUsers = mutableListOf()
                            oneUsers.addAll(tempList)
                        }
                    } else {
                        idNoMatchesTextView.text = "Нет лайкнувших профилей"
                        idNoMatchesTextView.visibility = View.VISIBLE
                    }
                } else {
                    idNoMatchesTextView.text = "Ошибка в запросе"
                    idNoMatchesTextView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun loadMatches(page: Int) {

        // что-то делаю не так, лень пока думать
        if (isBoth) {
            if (this::bothUsers.isInitialized) {
                adapter.addAll(bothUsers)
            } else {
                load(page)
            }
        } else {
            if (this::oneUsers.isInitialized) {
                adapter.addAll(oneUsers)
            } else {
                load(page)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("DEV", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DEV", "onDestroy")

    }

    override fun onPause() {
        super.onPause()
        Log.i("DEV", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i("DEV", "onResume")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("DEV", "onAttach")
    }

    override fun onStop() {
        super.onStop()
        Log.i("DEV", "onStop")
        if (swipeMessagesContainer != null && swipeMessagesContainer.isRefreshing)
            swipeMessagesContainer.isRefreshing = false
    }
}
