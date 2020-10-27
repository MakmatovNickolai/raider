package ru.raider.date.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.raider.date.models.Profile
import ru.raider.date.R
import ru.raider.date.databinding.CardViewProfileBinding


class ProfilesAdapter : RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder>() {

    private var profiles: MutableList<Profile>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProfileViewHolder(
            DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.card_view_profile,
                    parent,
                    false
            )
    )

    override fun getItemCount() = profiles?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        profiles?.let {
            holder.binding.profile = it[position]
            holder.binding.executePendingBindings()
        }
    }



    fun setsProfiles(profiles: MutableList<Profile>) {
        this.profiles = profiles

    }

    fun getProfiles(): MutableList<Profile> {
        return profiles!!
    }


    inner class ProfileViewHolder(val binding: CardViewProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

}