package com.moviejournal2

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
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
//        setContentView(R.layout.activity_view_friend)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityViewFriendBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        setContentView(binding.root)

        val b: Bundle? = getIntent().getExtras()
        val id = b?.getString("id")



        // Get database data
        if (id != null) {
            reference.child(id).get().addOnSuccessListener {
                if (it.exists()) {
                    binding.user.text = it.child("username").value.toString()
                    binding.favmovie.text = it.child("favmovie").value.toString().toEditable()

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
                    binding.title.setText(title)

                    // Chips
                    reference.child(id).child("genres").get().addOnSuccessListener { it2: DataSnapshot ->
                        if (it2.exists()) {
//                        val size = it2.childrenCount.toInt()
                            var counter = 0
                            var view: ChipGroup = binding.chipGroup

                            while (counter < 20) {
                                if (it2.child(counter.toString()).value != null) {
                                    var chip = Chip(this)
                                    chip.setCheckable(false)
                                    chip.setText(it2.child(counter.toString()).child("title").value.toString())
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



    }
}