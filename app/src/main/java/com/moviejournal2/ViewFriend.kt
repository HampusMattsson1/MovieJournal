package com.moviejournal2

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.databinding.ActivityViewFriendBinding
import com.moviejournal2.databinding.FragmentFriendsBinding
import com.moviejournal2.databinding.FragmentProfileBinding

class ViewFriend : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityViewFriendBinding
    private lateinit var storage: FirebaseStorage

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityViewFriendBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        setContentView(binding.root)

        val b: Bundle? = intent.extras
        val id = b?.getString("id")
        val name = b?.getString("name")

        // Get database data
        if (id != null) {
            reference.child(id).get().addOnSuccessListener {
                if (it.exists()) {
                    binding.user.text = it.child("username").value.toString()
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w342/${it.child("favmovie").value.toString()}")
                        .into(binding.favmovie)

                    // Get image
                    val path = "images/" + id + ".jpg"
                    val storageRef = storage.reference.child(path)
                    storageRef.downloadUrl.addOnSuccessListener { Uri->
                        Glide.with(this)
                            .load(Uri.toString())
                            .into(binding.profilePic)
                    }

                    // Set view title
                    val title = it.child("username").value.toString() + " profile"
                    binding.title.text = title

                    // Chips
                    reference.child(id).child("genres").get().addOnSuccessListener { it2: DataSnapshot ->
                        if (it2.exists()) {
//                        val size = it2.childrenCount.toInt()
                            var counter = 0
                            var view: ChipGroup = binding.chipGroup

                            while (counter < 20) {
                                if (it2.child(counter.toString()).value != null) {
                                    var chip = Chip(this)
                                    chip.isCheckable = false
                                    chip.text = it2.child(counter.toString()).child("title").value.toString()
                                    view.addView(chip)
                                }
                                counter += 1
                            }
                        }
                    }
                }
            }
        }


        // Unfriend button
        binding.unfriend.setOnClickListener { r ->
            if (id != null && name != null) {
                reference.child(globalVars.Companion.userID).child("friends").get().addOnSuccessListener {
                    if (it.exists()) {
                        reference.child(id).child("friends").get().addOnSuccessListener { it2 ->
                            if (it2.exists()) {
                                // Remove from user's friends list
                                it.children.forEach { c ->
                                    if (c.value.toString() == id) {
                                        c.ref.removeValue()
                                    }
                                }

                                // Remove from friend's friends list
                                it2.children.forEach { c ->
                                    if (c.value.toString() == globalVars.Companion.userID) {
                                        c.ref.removeValue()
                                    }
                                }

                                val output = "Unfriended " + name
                                Toast.makeText(this, output, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}