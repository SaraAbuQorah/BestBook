package com.example.bestbook

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class EditeInfoActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var userNameEd: TextView
    private lateinit var  editeEmail:ImageView
    private lateinit var editeUserName:ImageView
    private lateinit var editePhone:ImageView
    private lateinit var  editeEmailED:EditText
    private lateinit var editeUserNameED:EditText
    private lateinit var editePhoneED:EditText

    private lateinit var emailTV:TextView
    private lateinit var userNameTV:TextView
    private lateinit var phoneTV:TextView
    private lateinit var updateb:Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edite_info)
        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }

        updateb=findViewById(R.id.updatebutton)
        editeEmail=findViewById(R.id.EditeEmail)
        editePhone=findViewById(R.id.EditePhoneNum)
        editeUserName=findViewById(R.id.EditeUserName)
        emailTV=findViewById(R.id.emailTV)
        phoneTV=findViewById(R.id.phoneTV)
        userNameTV=findViewById(R.id.userNameTV)

        editeEmailED=findViewById(R.id.email)
        editeUserNameED=findViewById(R.id.Name)
        editePhoneED=findViewById(R.id.phone)

        editeUserName.setOnClickListener {
            if (editeUserNameED.visibility == View.GONE) {
                editeUserNameED.visibility = View.VISIBLE
            } else {
                editeUserNameED.visibility = View.GONE
            }
        }
        editeEmail.setOnClickListener {
            if (editeEmailED.visibility == View.GONE) {
                editeEmailED.visibility = View.VISIBLE
            } else {
                editeEmailED.visibility = View.GONE
            }
        }
        editePhone.setOnClickListener {
            if (editePhoneED.visibility == View.GONE) {
                editePhoneED.visibility = View.VISIBLE
            } else {
                editePhoneED.visibility = View.GONE
            }
        }

        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                userData?.let {
                    val username = it.username
                    val email=it.email
                    val phone=it.phoneNumber
                    userNameTV.text=username
                    emailTV.text = email
                    phoneTV.text=phone


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        updateb.setOnClickListener{
            val newUsername = editeUserNameED.text.toString()
            val newEmail = editeEmailED.text.toString()
            val newPhone = editePhoneED.text.toString()

            val updateData = HashMap<String, Any>()

            if (newUsername.isNotEmpty()) {
                updateData["username"] = newUsername
            }
            if (newEmail.isNotEmpty()) {
                updateData["email"] = newEmail
            }
            if (newPhone.isNotEmpty()) {
                updateData["phoneNumber"] = newPhone
            }
            database.updateChildren(updateData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Updated successfully", Toast.LENGTH_SHORT).show()                }
                .addOnFailureListener {
                    Toast.makeText(this, "Data update failed", Toast.LENGTH_SHORT).show()
                }

            if (newEmail.isNotEmpty()) {
                currentUser?.updateEmail(newEmail)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email update successfully", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "Email update failed", Toast.LENGTH_SHORT).show()

                        }
                    }
            }
            editeEmailED.text.clear()
            editePhoneED.text.clear()
            editeUserNameED.text.clear()
            editeUserNameED.visibility = View.GONE
            editePhoneED.visibility = View.GONE
            editeEmailED.visibility = View.GONE

        }
    }
}