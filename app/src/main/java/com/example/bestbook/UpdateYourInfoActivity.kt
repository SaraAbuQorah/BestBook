package com.example.bestbook

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class UpdateYourInfoActivity : AppCompatActivity() {
    private lateinit var userNameEd: TextView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_your_info)
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""

        userNameEd = findViewById(R.id.title)
        val database = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                userData?.let {
                    val username = it.username
                    userNameEd.text = username
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }

    }
}