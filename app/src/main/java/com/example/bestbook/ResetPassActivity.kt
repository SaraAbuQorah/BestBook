package com.example.bestbook

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ResetPassActivity : AppCompatActivity() {
    private lateinit var sendCode: TextView
    private lateinit var done: Button
    private lateinit var restLinearLayout: LinearLayout
    private lateinit var restB: Button
    private lateinit var codeET: EditText
    private lateinit var passET: EditText
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var userNameEd: TextView

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        val emailEditText = findViewById<EditText>(R.id.emailED)

        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }
        val auth = FirebaseAuth.getInstance()


        done = findViewById(R.id.doneB)
        done.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Email is required."
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { resetTask ->
                    if (resetTask.isSuccessful) {
                        // Password reset email sent successfully
                        Toast.makeText(
                            this,
                            "Password reset email sent.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Failed to send password reset email
                        Toast.makeText(
                            this,
                            "Password reset email failed to send.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}