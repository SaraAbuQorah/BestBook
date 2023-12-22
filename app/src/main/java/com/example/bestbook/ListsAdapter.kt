package com.example.bestbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListsAdapter (private val list: ArrayList<Book>) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {
    var onItemClick : ((Book) -> Unit)? =null

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lists_cardview, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemList = list[position]
        Glide.with(holder.bookImg)
            .load(itemList.imageUri)
            .into(holder.bookImg)
        holder.bookName.text = itemList.bookName
        holder.bookAuthorName.text = itemList.authorName
        holder.bookPrice.text = itemList.price
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(itemList)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.BookImgCardList)
        val bookName: TextView = itemView.findViewById(R.id.BookNameCardList)
        val bookAuthorName: TextView = itemView.findViewById(R.id.BookAuthorCardList)
        val bookPrice: TextView = itemView.findViewById(R.id.BookPriceCardList)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

