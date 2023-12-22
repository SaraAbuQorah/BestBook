package com.example.bestbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ContactActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var userNameEd: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }



    }
}
