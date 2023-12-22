package com.example.bestbook

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Math.random
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: BookPostAdapter
    private lateinit var recyclerView3: RecyclerView
    private lateinit var itemAdapter3: HomeListAdapter
    private var listData: ArrayList<Lists> = ArrayList()
    private var itemList: ArrayList<Book> = ArrayList()
    private var itemsIntent: ArrayList<Book> = ArrayList()
    private var itemListAfterDeleting: ArrayList<Book> = ArrayList()
    private var myBookList: ArrayList<Book> = ArrayList()
    private val imageRef = Firebase.storage.reference
    private lateinit var dialog: Dialog
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var userNameEd: TextView


    private lateinit var adventureStories: CardView
    private lateinit var crime: CardView
    private lateinit var fantasy: CardView
    private lateinit var horror: CardView
    private lateinit var mystery: CardView
    private lateinit var romance: CardView
    private lateinit var scienceFiction: CardView
    private lateinit var shortStories: CardView
    private lateinit var war: CardView
    private lateinit var historicalFiction: CardView
    private lateinit var science: CardView
    private lateinit var philosophy: CardView
    private lateinit var history: CardView
    private lateinit var biography: CardView
    private lateinit var academic: CardView

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
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        userNameEd = rootView.findViewById(R.id.title2)
        var search = rootView.findViewById<SearchView>(R.id.search)
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

        recyclerView = rootView.findViewById(R.id.BookPostRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fetchDataInFirebase()
        showProgressBar()
        fetchDataFromFirebase()
        listData = arrayListOf()

        //search bar

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return true
            }
        })

        adventureStories = rootView.findViewById(R.id.FictionAdventureStories)
        crime = rootView.findViewById(R.id.FictionCrime)
        fantasy = rootView.findViewById(R.id.FictionFantasy)
        horror = rootView.findViewById(R.id.FictionHorror)
        mystery = rootView.findViewById(R.id.FictionMystery)
        romance = rootView.findViewById(R.id.FictionRomance)
        scienceFiction = rootView.findViewById(R.id.FictionScienceFiction)
        shortStories = rootView.findViewById(R.id.FictionShortStories)
        war = rootView.findViewById(R.id.FictionWar)
        historicalFiction = rootView.findViewById(R.id.FictionHistoricalFiction)
        science = rootView.findViewById(R.id.NonFictionScience)
        philosophy = rootView.findViewById(R.id.NonFictionPhilosophy)
        history = rootView.findViewById(R.id.NonFictionHistory)
        biography = rootView.findViewById(R.id.NonFictionBiography)
        academic = rootView.findViewById(R.id.NonFictionAcademic)



        adventureStories.setOnClickListener {
            openCategory("Fiction-Adventure stories")
        }

        crime.setOnClickListener {
            openCategory("Fiction-Crime")

        }
        fantasy.setOnClickListener {
            openCategory("Fiction-Fantasy")

        }
        horror.setOnClickListener {
            openCategory("Fiction-Horror")

        }
        mystery.setOnClickListener {
            openCategory("Fiction-Mystery")

        }
        romance.setOnClickListener {
            openCategory("Fiction-Romance")

        }
        scienceFiction.setOnClickListener {
            openCategory("Fiction-Science fiction")

        }
        shortStories.setOnClickListener {
            openCategory("Fiction-Short stories")

        }
        war.setOnClickListener {
            openCategory("Fiction-War")

        }
        historicalFiction.setOnClickListener {
            openCategory( "Fiction-Historical fiction")

        }
        science.setOnClickListener {
            openCategory("Non-Fiction-Science")

        }
        philosophy.setOnClickListener {
            openCategory("Non-Fiction-Philosophy")

        }
        history.setOnClickListener {
            openCategory("Non-Fiction-History")

        }
        biography.setOnClickListener {
            openCategory("Non-Fiction-Biography")

        }
        academic.setOnClickListener {
            openCategory("Non-Fiction-Academic")

        }

        return rootView

    }

    private fun openCategory(category: String) {
        var categoryList: ArrayList<Book> = ArrayList()
        for (item in itemList){
            if (item.category.equals(category, ignoreCase = true)){
                categoryList.add(item)
            }
        }
        val intent = Intent(activity, ListBookActivity::class.java)
        intent.putExtra("bookData", categoryList)
        intent.putExtra("category",category)
        startActivity(intent)
    }


    private fun search(newText: String) {
    val searchGuides: java.util.ArrayList<Book> = java.util.ArrayList<Book>()
    for (g in itemList) {
        if (g.bookName!!.toLowerCase().contains(newText.toLowerCase())
            || g.authorName!!.toLowerCase().contains(newText.toLowerCase())
            || g.category!!.toLowerCase().contains(newText.toLowerCase())
        ) {
            searchGuides.add(g)
        }
    }
    val searchAdapter = BookPostAdapter(searchGuides)
    recyclerView.adapter = searchAdapter
    searchAdapter.onItemClick = {
        val intent = Intent(activity, TheBookActivity::class.java)
        intent.putExtra("book", it)
        startActivity(intent)
    }

}


private fun hideProgressBar() {
    dialog.dismiss()
}

private fun showProgressBar() {
    dialog = Dialog(requireContext())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog)
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()
}

private fun fetchDataInFirebase() = CoroutineScope(Dispatchers.IO).launch {
    val firebaseDatabaseAllBooks = FirebaseDatabase.getInstance()
    var databaseRefAllBooks = firebaseDatabaseAllBooks.reference.child("AllBooks")
    val coroutineScope = CoroutineScope(Dispatchers.Main) // Use Main dispatcher for UI updates

    databaseRefAllBooks =
        firebaseDatabaseAllBooks.reference.child("Users").child(uid).child("my books")
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

                        myBookList.add(book)

                    }

                }

            }

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })

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

                withContext(Dispatchers.Main) {
                    itemAdapter = BookPostAdapter(itemList)
                    recyclerView.adapter = itemAdapter
                    itemAdapter.onItemClick = {
                        val intent = Intent(activity, TheBookActivity::class.java)
                        intent.putExtra("book", it)
                        var categoryList: ArrayList<Book> = ArrayList()
                        for (item in itemList){
                            if (item.category.equals(it.category, ignoreCase = true)){
                                if(item!=it)
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
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
        HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
}
}



