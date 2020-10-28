package ru.raider.date.utils

import androidx.recyclerview.widget.DiffUtil
import ru.raider.date.models.User

class SpotDiffCallback(
        private val old: MutableList<User>,
        private val new: MutableList<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}