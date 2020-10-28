package ru.raider.date.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.raider.date.R
import ru.raider.date.models.User


class UsersAdapter{
/* : RecyclerView.Adapter<UsersAdapter.UserViewHolder>()
    private var profiles: MutableList<User>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(
            DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.card_view_profile,
                    parent,
                    false
            )
    )

    override fun getItemCount() = profiles?.size ?: 0

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        profiles?.let {
            holder.binding.user = it[position]
            holder.binding.executePendingBindings()
        }
    }



    fun setsProfiles(profiles: MutableList<User>) {
        this.profiles = profiles

    }

    fun getProfiles(): MutableList<User> {
        return profiles!!
    }


    inner class UserViewHolder(val binding: CardViewProfileBinding) :
        RecyclerView.ViewHolder(binding.root)
*/
}
