package ru.raider.date.adapter_models

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import convertImageUrl
import kotlinx.android.synthetic.main.card_view_profile.*
import kotlinx.android.synthetic.main.card_view_profile.view.*
import ru.raider.date.App
import ru.raider.date.R
import ru.raider.date.activities.ProfileActivity
import ru.raider.date.fragments.ProfileViewPhotosFragment
import ru.raider.date.network_models.User
import java.util.*

class ExploreUserItem(val user: User): Item<GroupieViewHolder>()  {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.idExploreUserName.text = user.name
        viewHolder.itemView.idExploreUserMainInfo.text = "${user.age} / ${user.sex} / ${user.city}"
        viewHolder.itemView.idExploreUserDescription.text = user.description
        val url = user.main_picture_url
        val newUrl = convertImageUrl(url)
        Picasso.get().load(newUrl).into(viewHolder.itemView.idExploreUserProfilePic)

        // не уверен, правильно ли добавлять обработчик событий здесь
        // TODO: 11.11.2020 подумать как расположить элементы так, чтобы и кликалось и скроллилось, пока linearlayout не может растянуться на всю высоту в scrollview 
        viewHolder.itemView.idProfileCardLayout.setOnClickListener {
            Collections.swap(user.pictureUrls, 0, user.main_picture_url!!.indexOf(user.main_picture_url!!))
            val profilePhotosFragment = ProfileViewPhotosFragment.newInstance(user)
            var context = viewHolder.itemView.context as FragmentActivity
            if (!profilePhotosFragment.isInLayout) {
                Log.i("DEV","isNotInLayout")
                context.supportFragmentManager
                    .beginTransaction()
                    .replace(
                    //.add(
                            // не уверен, что замещать здесь
                            R.id.container, profilePhotosFragment,
                            "ProfileViewPhotosFragment1"
                    )
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.i("DEV","isInLayout")
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.card_view_profile
    }
}