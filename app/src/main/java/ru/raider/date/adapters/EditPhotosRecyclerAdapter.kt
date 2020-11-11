package ru.raider.date.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import convertImageUrl
import kotlinx.android.synthetic.main.profile_edit_photo_recycler_item.view.*
import ru.raider.date.R
import ru.raider.date.network_models.PictureUrl
import java.util.*

class EditPhotosRecyclerAdapter(var items: MutableList<String> = mutableListOf(), val callback: Callback) : RecyclerView.Adapter<EditPhotosRecyclerAdapter.PictureUrlHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = PictureUrlHolder(LayoutInflater.from(parent.context).inflate(R.layout.profile_edit_photo_recycler_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PictureUrlHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setPictures(pictures: MutableList<String>) {
        this.items = pictures
    }

    fun getPictures(): MutableList<String> {
        return items
    }

    fun add(item: String) {
        items.add(item)
        //notifyDataSetChanged()
    }
    fun setAt(index: Int, item: String) {
        items[index] = item
        notifyDataSetChanged()
    }
    fun removeAt(index:Int) {
        items.removeAt(index)
        notifyDataSetChanged()
    }

    fun getPosition(item: String): Int {
        return items.indexOf(item)
    }

    fun getPicture(index:Int): String {
        return items[index]
    }
    fun swap(firstIndex:Int, secondIndex: Int) {
        Collections.swap(items, firstIndex, secondIndex)
        notifyDataSetChanged()
    }
    inner class PictureUrlHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String) {
            if (item.isNullOrEmpty()) {
                itemView.idProfileEditPhotoImageView.setImageResource(R.drawable.ic_baseline_add_24)
            } else {
                val newUrl = convertImageUrl(item)
                Picasso.get().load(newUrl).into(itemView.idProfileEditPhotoImageView)
            }
            itemView.setOnClickListener {
               if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: String)
    }
}