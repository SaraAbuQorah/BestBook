package com.example.bestbook

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
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
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FaveoretFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FaveoretFragment : Fragment() {
    private val itemList: ArrayList<Book> = ArrayList()
    private val imageRef = Firebase.storage.reference
    private lateinit var dialog: Dialog
    private lateinit var userNameEd: TextView
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: FavAdapter
    private val itemLis2: ArrayList<Book> = ArrayList()
    private val itemListAfterDeleting: ArrayList<Book> = ArrayList()
    private var myBookList: ArrayList<Book> = ArrayList()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        uid = currentUser?.uid ?: ""
        val rootView = inflater.inflate(R.layout.fragment_faveoret, container, false)

        recyclerView = rootView.findViewById(R.id.FavRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        showProgressBar()
        fetchDataFromFirebase()
        fetchDataFromFirebaseFav()

        return rootView
    }
    private fun hideProgressBar() {
        dialog.dismiss()
    }

    private fun showProgressBar() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun fetchDataFromFirebase() = CoroutineScope(Dispatchers.IO).launch {
        val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
        var databaseRefAllBooks = firebaseDatabaseAllBooks.reference.child("AllBooks")
        val coroutineScope = CoroutineScope(Dispatchers.Main) // Use Main dispatcher for UI updates

        databaseRefAllBooks.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    for (snapshot in dataSnapshot.children) {
                        val bookData = snapshot.getValue(Book::class.java)
                        bookData?.let {
                            val image = imageRef.child("/" + snapshot.key)
                            val url = image.downloadUrl.await()
                            val book = Book(
                                it.bookId,
                                it.bookName,
                                it.authorName,
                                it.pageNum,
                                it.originalCopy,
                                it.language,
                                it.price,
                                it.exchange,
                                it.category,
                                it.description,
                                it.bookerEmail,
                                it.bookerPhone,
                                url.toString()
                            )

                            itemList.add(book)

                        }
                    }

                    for (book in myBookList) {
                        for (i in itemList.indices) {
                            val item = itemList[i]
                            if (item.bookId == book.bookId) {
                                itemListAfterDeleting.add(item)
                            }
                        }
                    }
                    itemList.removeAll(itemListAfterDeleting)
                    hideProgressBar()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun fetchDataFromFirebaseFav() = CoroutineScope(Dispatchers.IO).launch {
        val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
        val databaseRefAllBooks = firebaseDatabaseAllBooks.reference.child("Users").child(uid).child("Fav")
        val coroutineScope = CoroutineScope(Dispatchers.Main) // Use Main dispatcher for UI updates

        databaseRefAllBooks.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coroutineScope.launch(Dispatchers.IO) {
                    for (snapshot in dataSnapshot.children) {
                        val bookData = snapshot.getValue(Book::class.java)
                        bookData?.let {
                            val image = imageRef.child("/" + snapshot.key)
                            val url = image.downloadUrl.await()
                            val book = Book(
                                it.bookId,
                                it.bookName,
                                it.authorName,
                                it.pageNum,
                                it.originalCopy,
                                it.language,
                                it.price,
                                it.exchange,
                                it.category,
                                it.description,
                                it.bookerEmail,
                                it.bookerPhone,
                                url.toString()
                            )
                            itemLis2.add(book)

                        }

                    }


                    withContext(Dispatchers.Main) {
                        itemAdapter = FavAdapter(itemLis2)
                        recyclerView.adapter = itemAdapter
                        itemAdapter.onItemClick={
                            val intent =Intent(activity ,TheBookActivity::class.java)
                            intent.putExtra("book",it)
                            var categoryList: ArrayList<Book> = ArrayList()
                            for (item in itemList){
                                if (item.category.equals(it.category, ignoreCase = true)) {

                                    if (item != it)
                                        categoryList.add(item)
                                }
                            }

                            intent.putExtra("bookData", categoryList)
                            startActivity(intent)
                        }
                        hideProgressBar()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FaveoretFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FaveoretFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}