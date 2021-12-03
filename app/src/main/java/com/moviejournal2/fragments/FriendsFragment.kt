package com.moviejournal2.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.R
import com.moviejournal2.ViewFriend
import com.moviejournal2.databinding.FragmentFriendsBinding
import com.moviejournal2.globalVars
import de.hdodenhof.circleimageview.CircleImageView
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

//                    val img = ImageView(activity)
                    val img = CircleImageView(activity)
                    img.layoutParams = binding.friendImg.layoutParams
                    val path = "images/" + u.value.toString() + ".jpg"
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
                    username.textSize = 25.toFloat()

                    reference.child(u.value.toString()).get().addOnSuccessListener { it2: DataSnapshot ->
                        if (it2.exists()) {
                            username.setText(it2.child("username").value.toString())
                        }
                    }

                    friendBox.addView(username)

                    // FriendBox layout params
                    friendBox.gravity = Gravity.CENTER_HORIZONTAL
                    friendBox.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                    friendBox.setPadding(50)

                    // Clicking on a friend
                    friendBox.setOnClickListener { f ->
                        val i = Intent(requireContext(), ViewFriend::class.java)
                        i.putExtra("id", u.value.toString())
                        startActivity(i)
                    }

                    binding.friends.addView(friendBox)
                }

                // Check incoming friend requests
                reference.child(globalVars.Companion.userID).child("requests").get().addOnSuccessListener { it2 ->
                    if (it2.exists() && it2.childrenCount.toInt() > 0) {
                        binding.bell.setImageResource(R.drawable.bell2)
                    }
                }
            }
        }


        // Search button
        binding.searchButton.setOnClickListener {
            var results = arrayOf<String>().toMutableList()
            var resultsRef = arrayOf<String>().toMutableList()
            var resultsIsfriend = arrayOf<Int>().toMutableList()

            // Get database data
            reference.get().addOnSuccessListener {
                if (it.exists()) {
                    reference.child(globalVars.Companion.userID).child("friends").get().addOnSuccessListener { it2 ->
                        if (it2.exists()) {
//                            var view: LinearLayout = binding.friends
                            val me = it.child(globalVars.Companion.userID.toString()).child("username").value.toString()

                            // Loop through all users
                            it.children.forEach { u: DataSnapshot ->
                                val res = u.child("username").value.toString()
                                if (res == binding.userSearch.text.toString() &&
                                    res != me
                                ) {
                                    // Loop through user's friends
                                    var friend = false
                                    it2.children.forEach { f: DataSnapshot ->
                                        if (it.child(f.value.toString()).child("username").value.toString() == binding.userSearch.text.toString()) {
                                            friend = true
                                        }
                                    }
                                    results.add(res)
                                    resultsRef.add(u.key.toString())
                                    if (friend) {
                                        resultsIsfriend.add(1)
                                    } else {
                                        resultsIsfriend.add(0)
                                    }
                                }
                            }

                            if (results.isEmpty()) {
                                Toast.makeText(activity, "No users found", Toast.LENGTH_SHORT).show()
                            } else {
                                // Dialog popup initialization
                                var d: Dialog = Dialog(requireContext())
                                d.setTitle("Search results")
                                d.setContentView(R.layout.template_search)

                                var view = d.findViewById<LinearLayout>(R.id.dialog)
                                var counter = 0

                                // Loop through search results
                                results.forEach { r: String ->
                                    val box = LinearLayout(context)
                                    val l1 = d.findViewById<LinearLayout>(R.id.dialog)
                                    box.layoutParams = l1.layoutParams

                                    val img = ImageView(context)
                                    val l2 = d.findViewById<ImageView>(R.id.userImg)
//                                    val img = CircleImageView(context)
//                                    val l2 = d.findViewById<CircleImageView>(R.id.userImg)
                                    img.layoutParams = l2.layoutParams

                                    val userid = resultsRef[counter]

                                    val path = "images/" + userid + ".jpg"
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

                                    if (resultsIsfriend[counter] == 1) {
                                        add.setImageResource(R.drawable.done)
                                    } else {
                                        add.setImageResource(R.drawable.add)
                                    }

                                    // Add button listener
                                    add.setOnClickListener { a ->
                                        // Check if friend request is already sent
                                        var alreadySent = false

                                        var counter = 0
                                        while (counter < it.child(userid).child("requests").childrenCount) {
                                            val id = it.child(userid).child("requests").child(counter.toString()).value.toString()
                                            if (id == globalVars.Companion.userID) {
                                                alreadySent = true
                                                add.setImageResource(R.drawable.done)
                                                break
                                            }
                                            counter += 1
                                        }

                                        if (alreadySent == false) {
                                            var index = 0
                                            while (index < it.child(userid).child("requests").childrenCount) {
                                                if (it.child(userid).child("requests").child(index.toString()).value == null) {
                                                    break
                                                }
                                                index += 1
                                            }
                                            reference.child(userid).child("requests").child(index.toString()).setValue(globalVars.Companion.userID)

                                            val output = "Friend request sent to " + r
                                            Toast.makeText(activity, output, Toast.LENGTH_SHORT).show()
                                            add.setImageResource(R.drawable.done)
                                        } else {
                                            val output = "Friend request already sent to  " + r
                                            Toast.makeText(activity, output, Toast.LENGTH_SHORT).show()
                                        }
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


        // Friend requests button
        binding.bell.setOnClickListener {
            var results = arrayOf<String>().toMutableList()
            var resultsRef = arrayOf<String>().toMutableList()

            // Get database data
            reference.get().addOnSuccessListener {
                if (it.exists()) {
                    reference.child(globalVars.Companion.userID).child("requests").get().addOnSuccessListener { it2 ->

                        if (it2.childrenCount > 0) {

                            // Dialog popup initialization
                            var d: Dialog = Dialog(requireContext())
                            d.setTitle("Friend requests")
                            d.setContentView(R.layout.template_friend_requests)

                            var view = d.findViewById<LinearLayout>(R.id.dialog)
                            var counter = 0

                            it2.children.forEach { req ->

                                val box = LinearLayout(context)
                                val l1 = d.findViewById<LinearLayout>(R.id.dialog)
                                box.layoutParams = l1.layoutParams

                                val img = ImageView(context)
                                val l2 = d.findViewById<ImageView>(R.id.userImg)
                                img.layoutParams = l2.layoutParams
                                img.setImageResource(R.drawable.profile)

                                val userid = it2.child(counter.toString()).value.toString()

                                val path = "images/" + userid + ".jpg"
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
                                username.setText(it.child(userid).child("username").value.toString())
                                box.addView(username)

                                val accept = ImageView(context)
                                val l4 = d.findViewById<ImageView>(R.id.userAccept)
                                accept.layoutParams = l4.layoutParams
                                accept.setImageResource(R.drawable.done)

                                // Accept button listener
                                accept.setOnClickListener { a ->

                                    // Add friend to my friends list
                                    var index1 = 0
                                    val ref1 = it.child(globalVars.Companion.userID).child("friends")
                                    while (index1 < ref1.childrenCount) {
                                        if (ref1.child(index1.toString()).value == null) {
                                            break
                                        }
                                        index1 += 1
                                    }
                                    ref1.child(index1.toString()).ref.setValue(req.value.toString()).addOnSuccessListener { r1 ->
                                        // Add me to the other user's friends list
                                        var index2 = 0
                                        val ref2 = it.child(userid).child("friends")
                                        while (index2 < ref2.childrenCount) {
                                            if (ref2.child(index2.toString()).value == null) {
                                                break
                                            }
                                            index2 += 1
                                        }
                                        ref2.child(index2.toString()).ref.setValue(globalVars.Companion.userID).addOnSuccessListener { r2 ->
                                            // Remove friends request
                                            it2.ref.removeValue().addOnSuccessListener { r3 ->
                                                Toast.makeText(activity, "Friend request accepted", Toast.LENGTH_SHORT).show()
                                                box.visibility = GONE
                                            }
                                        }
                                    }

                                }

                                box.addView(accept)

                                val deny = ImageView(context)
                                val l5 = d.findViewById<ImageView>(R.id.userDeny)
                                deny.layoutParams = l5.layoutParams
                                deny.setImageResource(R.drawable.ignore)

                                // Deny button listener
                                deny.setOnClickListener { d ->
                                    it2.ref.removeValue()
                                    Toast.makeText(activity, "Friend required denied", Toast.LENGTH_SHORT).show()
                                }

                                box.addView(deny)

                                view.addView(box)
                                counter += 1
                            }


                            // Close button onclick
                            d.findViewById<TextView>(R.id.closeDialog).setOnClickListener { c ->
                                d.dismiss()
                            }

                            // Show dialog
                            d.show()

                        } else {
                            Toast.makeText(activity, "No incoming friend requests", Toast.LENGTH_SHORT).show()
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