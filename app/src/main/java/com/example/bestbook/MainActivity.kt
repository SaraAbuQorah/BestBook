package com.example.bestbook

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private lateinit var register : TextView
    private lateinit var logInButton:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var forgotPass:TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        forgotPass=findViewById(R.id.forgotPasswordTV)
        forgotPass.setOnClickListener {
            val intent = Intent(this, ResetPassActivity::class.java)
            startActivity(intent)
        }
        auth = Firebase.auth
        val edittext = findViewById<EditText>(R.id.email)
        val editTextPass=findViewById<EditText>(R.id.pass)
        logInButton=findViewById(R.id.loginButton)
        logInButton.setOnClickListener{
            val email = edittext.text.toString()
            val pass = editTextPass.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uid = auth.currentUser?.uid.toString()
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()


                    } else {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
        register=findViewById(R.id.Register)
        register.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }
}