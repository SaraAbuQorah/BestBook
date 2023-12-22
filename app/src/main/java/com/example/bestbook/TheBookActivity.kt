package com.example.bestbook

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class TheBookActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private var isImageClicked = false
    private var fav: ArrayList<Book> = ArrayList()
    private var itemList: ArrayList<Book> = ArrayList()
    private lateinit var favoriteImg: ImageView
    private lateinit var theBookImg: ImageView
    private lateinit var bookNane: TextView
    private lateinit var authorName: TextView
    private lateinit var description: TextView
    private lateinit var price: TextView
    private lateinit var originalCopy: TextView
    private lateinit var exchangeable: TextView
    private lateinit var pageNum: TextView
    private lateinit var bookLanguage: TextView
    private lateinit var bookType: TextView
    private lateinit var BookerEmail: TextView
    private lateinit var BookerPhone: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: BookPostAdapter
    private lateinit var relatedBook:TextView


    private var isFav = false
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    private lateinit var hiddenArrow1: ImageView
    private lateinit var hiddenArrow2: ImageView
    private lateinit var hiddenView1: LinearLayout
    private lateinit var hiddenView2: LinearLayout


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_book)
        favoriteImg = findViewById(R.id.FavoriteImg)
        hiddenArrow1 = findViewById(R.id.hiddenArrow1)
        hiddenArrow2 = findViewById(R.id.hiddenArrow2)
        hiddenView1 = findViewById(R.id.hiddenView1)
        hiddenView2 = findViewById(R.id.hiddenView2)

        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""


        if (intent.hasExtra("bookData")){
            itemList = intent.getParcelableArrayListExtra<Book>("bookData")!!


        }

        relatedBook=findViewById(R.id.relatedBook)
        if(itemList.size==0 )
            relatedBook.visibility=View.GONE
        else
            relatedBook.visibility=View.VISIBLE


        recyclerView =findViewById(R.id.BookPostRecommendationRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        itemAdapter = BookPostAdapter(itemList)
        recyclerView.adapter = itemAdapter
        itemAdapter.onItemClick = {
            val intent = Intent(this, TheBookActivity::class.java)
            intent.putExtra("book", it)
            startActivity(intent)


        }
        hiddenArrow1.setOnClickListener {
            if (hiddenView1.visibility == View.GONE) {
                hiddenView1.visibility = View.VISIBLE
                hiddenArrow1.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                hiddenView1.visibility = View.GONE
                hiddenArrow1.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }
        hiddenArrow2.setOnClickListener {
            if (hiddenView2.visibility == View.GONE) {
                hiddenView2.visibility = View.VISIBLE
                hiddenArrow2.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)

            } else {
                hiddenView2.visibility = View.GONE
                hiddenArrow2.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }
        val arrowBack = findViewById<CardView>(R.id.arrow_back1)
        arrowBack.setOnClickListener {
            finish()
        }
        favoriteImg = findViewById(R.id.FavoriteImg)



        theBookImg = findViewById(R.id.TheBookImg)
        bookNane = findViewById(R.id.bookName)
        authorName = findViewById(R.id.authorName)
        description = findViewById(R.id.Description)
        price = findViewById(R.id.Price)
        originalCopy = findViewById(R.id.OriginalCopy)
        exchangeable = findViewById(R.id.Exchangeable)
        pageNum = findViewById(R.id.pageNum)
        bookLanguage = findViewById(R.id.Language)
        bookType = findViewById(R.id.bookType)
        BookerEmail = findViewById(R.id.BookerEmail)
        BookerPhone = findViewById(R.id.BookerPhone)

        val bookData = intent.getParcelableExtra<Book>("book")
        Glide.with(this)
            .load(bookData?.imageUri)
            .placeholder(R.drawable.dan)
            .error(R.drawable.ic_baseline_person_24)
            .priority(Priority.HIGH)
            .into(theBookImg)

        bookNane.text = bookData?.bookName
        authorName.text = bookData?.authorName
        pageNum.text = bookData?.pageNum
        description.text = bookData?.description
        price.text = bookData?.price
        if (bookData?.originalCopy.toString() == "true")
            originalCopy.text = "Yes"
        else
            originalCopy.text = "No"
        if (bookData?.exchange.toString() == "true")
            exchangeable.text = "Yes"
        else
            exchangeable.text = "No"
        bookLanguage.text = bookData?.language
        bookType.text = bookData?.category

        BookerEmail.text = bookData?.bookerEmail
        favoriteImg.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)


        if (bookData != null) {
            fetchDataFromFirebase(bookData)
        }

        if (bookData?.bookerEmail.isNullOrEmpty()) {
            val database = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val emaildata = snapshot.getValue(User::class.java)
                    emaildata?.let {
                        val emailB = it.email
                        BookerEmail.text = emailB
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        val database = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phonedata = snapshot.getValue(User::class.java)
                phonedata?.let {
                    val phoneB = it.phoneNumber
                    if (bookData?.bookerPhone.isNullOrEmpty())
                        BookerPhone.text = phoneB
                    else
                        BookerPhone.text = bookData?.bookerPhone

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
        val databaseRefAllBooks =
            firebaseDatabaseAllBooks.reference.child("Users").child(uid).child("Fav")


        favoriteImg.setOnClickListener {
            if (!isFav) {
                isFav = true
                favoriteImg.setBackgroundResource(R.drawable.ic_baseline_favorite_24)

                auth = Firebase.auth
                val currentUser: FirebaseUser? = auth.currentUser
                uid = currentUser?.uid ?: ""
                               databaseRefAllBooks.push()
                isImageClicked = !isImageClicked
                if (isImageClicked) {
                    val book = Book(
                        bookData?.bookId,
                        bookData?.bookName,
                        bookData?.authorName,
                        bookData?.pageNum,
                        bookData?.originalCopy,
                        bookData?.language,
                        bookData?.price,
                        bookData?.exchange,
                        bookData?.category,
                        bookData?.description,
                        bookData?.bookerEmail,
                        bookData?.bookerPhone,
                        bookData?.imageUri.toString()

                    )
                    val newBookRefUserList = bookData?.bookId?.let { it1 ->
                        databaseRefAllBooks.ref.child(
                            it1
                        )
                    }
                    newBookRefUserList?.setValue(book)
                }
            } else {
                isFav = false
                databaseRefAllBooks.child(bookData?.bookId ?: "").removeValue()
                favoriteImg.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
            }


        }

    }

    private fun fetchDataFromFirebase(bookData: Book) = CoroutineScope(Dispatchers.IO).launch {

        val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
        val databaseRefAllBooks =
            firebaseDatabaseAllBooks.reference.child("Users").child(uid).child("Fav")
        val coroutineScope = CoroutineScope(Dispatchers.Main) // Use Main dispatcher for UI updates


        databaseRefAllBooks.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    for (snapshot in dataSnapshot.children) {
                        val favBook = snapshot.getValue(Book::class.java)
                        favBook?.let {
                            if (it.bookId == bookData?.bookId) {
                                isFav = true
                            }
                        }

                    }
                    if (isFav) {
                        favoriteImg.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                    } else {
                        favoriteImg.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isFav = false
            }

        })
    }

    private fun hideProgressBar() {
        dialog.dismiss()
    }

    private fun showProgressBar() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


}