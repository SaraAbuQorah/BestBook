package com.example.bestbook

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class NewBooksActivity : AppCompatActivity() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: Spinner
    private lateinit var userNameEd: TextView
    private lateinit var addButton: Button
    private var selectedCategory: String? = null
    private lateinit var bookNameEditText: EditText
    private lateinit var authorNameEditText: EditText
    private lateinit var pageNumEditText: EditText
    private lateinit var originalCopyEditText: CheckBox
    private lateinit var exchangeEditText: CheckBox
    private lateinit var languageEditText: RadioButton
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var imageUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var bookerEmailEditText:EditText
    private lateinit var bookerPhoneNumberEditText:EditText

    private lateinit var uploadImg: ImageView
    private lateinit var dialog: Dialog
    private lateinit var alertDialog: Dialog
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_books)
        uploadImg = findViewById(R.id.img3)
        spinner = findViewById(R.id.spinner)
        bookNameEditText = findViewById(R.id.BookName)
        authorNameEditText = findViewById(R.id.Name)
        pageNumEditText = findViewById(R.id.PageNum)
        originalCopyEditText = findViewById(R.id.checkBoxOriginalCopy)
        languageEditText = findViewById(R.id.radioMale)
        priceEditText = findViewById(R.id.Price)
        exchangeEditText = findViewById(R.id.checkBoxExchanging)
        descriptionEditText = findViewById(R.id.Description)
        bookerEmailEditText=findViewById(R.id.BookerEmail)
        bookerPhoneNumberEditText=findViewById(R.id.BookerPhone)
        uploadImg.setImageResource(R.drawable.upload)
        imageUri = Uri.parse("your_image_uri")

        val arrowBack = findViewById<LinearLayout>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }

        uploadImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""


        addButton = findViewById(R.id.AddBook)
        addButton.setOnClickListener {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val storage = FirebaseStorage.getInstance()
            databaseRef = firebaseDatabase.reference
                .child("Users")
                .child(uid)
                .child("my books")
            val bookName = bookNameEditText.text.toString()
            val authorName = authorNameEditText.text.toString()
            val pageNum = pageNumEditText.text.toString()
            val originalCopy = originalCopyEditText.isChecked
            val language = if (languageEditText.isChecked) "Arabic" else "English"
            val price = priceEditText.text.toString()
            val exchange = exchangeEditText.isChecked
            val description = descriptionEditText.text.toString()
            val bookerEmail = bookerEmailEditText.text.toString()
            val bookerPhone = bookerPhoneNumberEditText.text.toString()
            val bookId = databaseRef.push().key ?: ""
            val reference = storage.reference.child(bookId)
            reference.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->

                        selectedCategory?.let { category ->

                            val book = Book(
                                bookId,
                                bookName,
                                authorName,
                                pageNum,
                                originalCopy,
                                language,
                                price,
                                exchange,
                                category,
                                description,
                                bookerEmail,
                                bookerPhone,
                                imageUri.toString()

                            )
                            val newBookRefUserList = databaseRef.child(bookId)
                            newBookRefUserList.setValue(book)

                            val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
                            val databaseRefAllBooks = firebaseDatabaseAllBooks.reference
                                .child("AllBooks")
                            val newBookRefAllBooks = databaseRefAllBooks.child(bookId)
                            newBookRefAllBooks.setValue(book)
                        }
                    }

                }

                setOnCheckedChangeListener()


            }

            bookNameEditText.text.clear()
            authorNameEditText.text.clear()
            pageNumEditText.text.clear()
            originalCopyEditText.isChecked = false
            languageEditText.isChecked = false
            priceEditText.text.clear()
            exchangeEditText.isChecked = false
            descriptionEditText.text.clear()
            uploadImg.setImageResource(R.drawable.upload)
            bookerEmailEditText.text.clear()
            bookerPhoneNumberEditText.text.clear()
        }
        val items = arrayOf(
            "Fiction-Adventure stories",
            "Fiction-Crime",
            "Fiction-Fantasy",
            "Fiction-Horror",
            "Fiction-Mystery",
            "Fiction-Romance",
            "Fiction-Science fiction",
            "Fiction-Short stories",
            "Fiction-War",
            "Fiction-Historical fiction",
            "Non-Fiction-Science",
            "Non-Fiction-Philosophy",
            "Non-Fiction-History",
            "Non-Fiction-Biography",
            "Non-Fiction-Academic"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }


    private fun setOnCheckedChangeListener() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                imageUri = data.data!!
                uploadImg.setImageURI(imageUri)

            }
        }else {
            uploadImg.setImageResource(R.drawable.upload)

        }
    }
    fun showProgressBar() {
        dialog=Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    private fun hideProgressBar() {
        dialog.dismiss()
    }

}