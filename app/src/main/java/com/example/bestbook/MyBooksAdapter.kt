package com.example.bestbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyBooksAdapter (private val list: ArrayList<Book>) : RecyclerView.Adapter<MyBooksAdapter.ViewHolder>() {
    var onItemClick : ((Book) -> Unit)? =null

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_books_card, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemList = list[position]
//        loadUriImageIntoImageView(itemList.imageUri, holder.bookImg)
        Glide.with(holder.bookImg)
            .load(itemList.imageUri)
            .into(holder.bookImg)
        holder.bookName.text = itemList.bookName
        holder.bookAuthorName.text = itemList.authorName
        holder.bookPrice.text = itemList.price
        holder.editeBook.setOnClickListener{

            val intent = Intent(holder.editeBook.context, TheBookActivity::class.java)
            holder.editeBook.context.startActivity(intent)

        }
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(itemList)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.BookImgCard1)
        val bookName: TextView = itemView.findViewById(R.id.BookNameCard1)
        val bookAuthorName: TextView = itemView.findViewById(R.id.BookAuthorCard1)
        val bookPrice: TextView = itemView.findViewById(R.id.BookPriceCard1)
        val editeBook:ImageView =itemView.findViewById(R.id.EditeBook)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun loadUriImageIntoImageView(imageUri: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(imageUri)
            .into(imageView)
    }
}

