package ru.raider.date.utils

import androidx.recyclerview.widget.DiffUtil
import ru.raider.date.models.Profile

class SpotDiffCallback(
        private val old: MutableList<Profile>,
        private val new: MutableList<Profile>
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