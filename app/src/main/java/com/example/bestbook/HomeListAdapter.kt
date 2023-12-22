package com.example.bestbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HomeListAdapter (private val list: ArrayList<Lists>) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {
    var onItemClick : ((Lists) -> Unit)? =null


    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_list_cardview, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemList = list[position]

        holder.bookName.text = itemList.ListsBookName

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(itemList)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val bookName: TextView = itemView.findViewById(R.id.listNameHome)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

