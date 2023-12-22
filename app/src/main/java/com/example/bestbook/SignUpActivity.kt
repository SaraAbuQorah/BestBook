package com.example.bestbook

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var loginText: TextView
    private lateinit var signUpButton: Button
    private lateinit var userNameEd: EditText
    private lateinit var emailEd: EditText
    private lateinit var passwordEd: EditText
    private lateinit var phoneNumberEd: EditText
    private lateinit var database : DatabaseReference
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        FirebaseApp.initializeApp(this)

        auth = Firebase.auth
        userNameEd = findViewById(R.id.username)
        emailEd = findViewById(R.id.email)
        passwordEd= findViewById(R.id.pass)
        phoneNumberEd=findViewById(R.id.phone)
        signUpButton = findViewById(R.id.signupButton)

        signUpButton.setOnClickListener {
            val userName = userNameEd.text.toString().trim()
            val email = emailEd.text.toString().trim()
            val password = passwordEd.text.toString().trim()
            val phoneNumber = phoneNumberEd.text.toString().trim()
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "Please enter phone Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if ( password.length<=8) {
                Toast.makeText(this, " password must be 8 or more characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pattern = Pattern   .compile(".*[a-zA-Z].*")
            if (!pattern.matcher(password).matches()) {
                Toast.makeText(this, "Password must contain at least one alphabetic character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uid = auth.currentUser?.uid.toString()
            database = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val newUserId = task.result?.user?.uid

                        database = FirebaseDatabase.getInstance().getReference("Users")
                        val user = User(
                            userName,
                            email,
                            phoneNumber,

                        )
                        if (newUserId != null) {
                            database.child(newUserId).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    userNameEd.text.clear()
                                    emailEd.text.clear()
                                    phoneNumberEd.text.clear()
                                    passwordEd.text.clear()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this,
                                        "Sign up failed: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        loginText = findViewById(R.id.loginAc)
        loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}