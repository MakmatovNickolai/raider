package ru.raider.date.models

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import convertImageUrl
import kotlinx.android.synthetic.main.card_view_profile.view.*
import ru.raider.date.R

class ExploreUserItem(val user:User): Item<GroupieViewHolder>()  {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.idExploreUserNameAndAge.text = user.name + ", " + user.age
        viewHolder.itemView.idSex.text = user.sex
        val url = user.pictureUrl
        val newUrl = convertImageUrl(url)
        Picasso.get().load(url).into(viewHolder.itemView.idExploreUserProfilePic)
    }

    override fun getLayout(): Int {
        return R.layout.card_view_profile
    }
}