package com.moviejournal2.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.EditProfile
import com.moviejournal2.R
import com.moviejournal2.ViewFriend
import com.moviejournal2.databinding.FragmentFriendsBinding
import com.moviejournal2.globalVars
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment : Fragment() {
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

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentFriendsBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        reference.child(globalVars.Companion.userID).child("friends").get().addOnSuccessListener {
            if (it.exists()) {

                // Find users by username
                it.children.forEach { u: DataSnapshot ->
                    var friendBox = LinearLayout(activity)
                    friendBox.layoutParams = binding.friendsTemplate.layoutParams

                    val img = ImageView(activity)
                    img.layoutParams = binding.friendImg.layoutParams
                    val path = "images/" + it.value.toString() + ".jpg"
                    val storageRef = storage.reference.child(path)
                    storageRef.downloadUrl.addOnSuccessListener { Uri->
                        Glide.with(this)
                            .load(Uri.toString())
                            .into(img)
                    }.addOnFailureListener { Uri->
                        img.setImageResource(R.drawable.profile)
                    }
                    friendBox.addView(img)

                    val username = TextView(getActivity())
                    username.layoutParams = binding.friendName.layoutParams
                    username.textSize = 20.toFloat()
//
                    reference.child(u.value.toString()).get().addOnSuccessListener { it2: DataSnapshot ->
                        if (it2.exists()) {
                            username.setText(it2.child("username").value.toString())
                        }
                    }

                    friendBox.addView(username)

                    friendBox.gravity = Gravity.CENTER_HORIZONTAL
                    friendBox.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                    friendBox.setPadding(35)

                    // Clicking on a friend
                    friendBox.setOnClickListener { f ->
                        val i = Intent(requireContext(), ViewFriend::class.java)
                        i.putExtra("id", u.value.toString())
                        startActivity(i)
                    }

                    binding.friends.addView(friendBox)
                }
            }
        }


        // Search button
        binding.searchButton.setOnClickListener {
            var results = arrayOf<String>().toMutableList()
            var resultsRef = arrayOf<String>().toMutableList()

            // Get database data
            reference.get().addOnSuccessListener {
                if (it.exists()) {
                    reference.child(globalVars.Companion.userID).child("friends").get().addOnSuccessListener { it2 ->
                        if (it2.exists()) {
                            var view: LinearLayout = binding.friends
                            val me = it.child(globalVars.Companion.userID.toString()).child("username").value.toString()

                            // Loop through all users
                            it.children.forEach { u: DataSnapshot ->
                                val res = u.child("username").value.toString()
                                if (res == binding.userSearch.text.toString() &&
                                    res != me
                                ) {
                                    // Loop through user's friends
                                    var valid = true
                                    it2.children.forEach { f: DataSnapshot ->
                                        if (it.child(f.value.toString()).child("username").value.toString() == binding.userSearch.text.toString()) {
                                            valid = false
                                        }
                                    }
                                    if (valid) {
                                        results.add(u.child("username").value.toString())
                                        resultsRef.add(u.key.toString())
                                    }
                                }
                            }

                            if (results.isEmpty()) {
                                Toast.makeText(activity, "No users found", Toast.LENGTH_SHORT).show()
                            } else {
                                // Dialog popup initialization
                                var d: Dialog = Dialog(requireContext())
                                d.setTitle("Search results")
                                d.setContentView(R.layout.friends_template)

                                view = d.findViewById<LinearLayout>(R.id.dialog)
                                var counter = 0

                                // Loop through search results
                                results.forEach { r: String ->
                                    val box = LinearLayout(context)
                                    val l1 = d.findViewById<LinearLayout>(R.id.dialog)
                                    box.layoutParams = l1.layoutParams

                                    val img = ImageView(context)
                                    val l2 = d.findViewById<ImageView>(R.id.userImg)
                                    img.layoutParams = l2.layoutParams
                                    Log.d("test", resultsRef.toString())
                                    val path = "images/" + resultsRef[counter] + ".jpg"
                                    val storageRef = storage.reference.child(path)
                                    storageRef.downloadUrl.addOnSuccessListener { Uri->
                                        Glide.with(this)
                                            .load(Uri.toString())
                                            .into(img)
                                    }.addOnFailureListener { Uri->
                                        img.setImageResource(R.drawable.profile)
                                    }
                                    box.addView(img)

                                    val username = TextView(context)
                                    val l3 = d.findViewById<TextView>(R.id.userText)
                                    username.layoutParams = l3.layoutParams
                                    username.setText(r)
                                    box.addView(username)

                                    val add = ImageView(context)
                                    val l4 = d.findViewById<ImageView>(R.id.userAdd)
                                    add.layoutParams = l4.layoutParams
                                    add.setImageResource(R.drawable.add)
                                    // Add button listener
                                    add.setOnClickListener { a ->
                                        val output = "Friend request sent to " + r
                                        Toast.makeText(activity, output, Toast.LENGTH_SHORT).show()
                                    }

                                    box.addView(add)

                                    view.addView(box)
                                    counter += 1
                                }

                                // Close button onclick
                                d.findViewById<TextView>(R.id.closeDialog).setOnClickListener { c ->
                                    d.dismiss()
                                }

                                // Show dialog
                                d.show()
                            }

                        }
                    }
                }
            }
        }

        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}