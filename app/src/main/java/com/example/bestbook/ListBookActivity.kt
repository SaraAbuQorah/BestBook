package com.example.bestbook

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ListBookActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var userNameEd: TextView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var itemAdapter2: ListsAdapter
    private var itemList: ArrayList<Book> = ArrayList()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_book)

        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }
        if (intent.hasExtra("bookData")) {
            itemList = intent.getParcelableArrayListExtra<Book>("bookData")!!
        }
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""

        userNameEd = findViewById(R.id.title)
        val categoryName = intent.getStringExtra("category")
        userNameEd.text = categoryName

        var categoryList: ArrayList<Book> = ArrayList()

        recyclerView2 = findViewById(R.id.ListList)
        recyclerView2.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemAdapter2 = ListsAdapter(itemList)
        recyclerView2.adapter = itemAdapter2
        itemAdapter2.onItemClick = {
            val intent = Intent(this, TheBookActivity::class.java)
            intent.putExtra("book", it)
            for (item in itemList) {
                if (item != it)
                    categoryList.add(item)

            }
            intent.putExtra("bookData", categoryList)
            startActivity(intent)

        }

    }
}