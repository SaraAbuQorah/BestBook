package com.example.bestbook

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BooksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    private lateinit var userNameEd: TextView
    private lateinit var uid: String
    private lateinit var auth: FirebaseAuth
    private lateinit var logout:CardView
    private lateinit var deleteAccount:CardView
    private lateinit var yourInfo:CardView
    private lateinit var shipmentCompany:CardView
    private lateinit var contact:CardView


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
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        logout=rootView.findViewById(R.id.logOutCardView)
        logout.setOnClickListener{
            showLogoutConfirmationDialog()
        }
        deleteAccount=rootView.findViewById(R.id.deleteAccountCardView)
        deleteAccount.setOnClickListener{
            showDeleteConfirmationDialog()
            infoEdit()
        }
        yourInfo=rootView.findViewById(R.id.yourInfo)
        yourInfo.setOnClickListener{
            val intent=Intent(context, EditeInfoActivity::class.java)
            startActivity(intent)
        }
        contact=rootView.findViewById(R.id.contact)
        contact.setOnClickListener{
            val intent=Intent(context, ContactActivity::class.java)
            startActivity(intent)
        }
        return rootView
    }

    private fun infoEdit() {
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = "Jane Q. User"
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                }
            }
    }

    private fun showDeleteConfirmationDialog() {
        context?.let { context ->
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage("Are You Sure You Want To Delete Your Account?")
            alertDialogBuilder.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                auth = Firebase.auth
                val currentUser: FirebaseUser? = auth.currentUser
                uid = currentUser?.uid ?: ""



                currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                            databaseReference.removeValue()
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        context?.let { context ->
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage("Are You Sure You Want To Logout?")
            alertDialogBuilder.setPositiveButton("logout") { _: DialogInterface, _: Int ->
                startActivity(Intent(context, MainActivity::class.java))
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BooksFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}