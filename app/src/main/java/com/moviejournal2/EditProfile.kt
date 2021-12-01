package com.moviejournal2

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.databinding.ActivityEditProfileBinding
import com.moviejournal2.fragments.EditProfileFragment
import java.io.ByteArrayOutputStream

class EditProfile : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var storage: FirebaseStorage

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_profile)


        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        setContentView(binding.root)

        // Chip array & variables
        var size = 0
        var counter = 0
        var chips = List(size) { Chip(this) }.toMutableList()
        var chipsID = arrayOf<String>().toMutableList()


        // Get database data
        reference.child(globalVars.Companion.userID).get().addOnSuccessListener {
            if (it.exists()) {
                binding.username.text = it.child("username").value.toString().toEditable()
//                binding.favmovie.text = it.child("favmovie").value.toString().toEditable()

                database.getReference("genres").get().addOnSuccessListener { it2: DataSnapshot ->
                    if (it2.exists()) {
//                        Toast.makeText(activity, "Genres found", Toast.LENGTH_SHORT).show()
                        size = it2.childrenCount.toInt()
                        counter = 0
                        var view: ChipGroup = binding.chipGroup

                        while (counter < size) {
                            var chip = Chip(this)
                            chip.setCheckable(true)

                            val s1 = it.child("genres").child(counter.toString()).child("title").value.toString()
                            val s2 = it2.child(counter.toString()).child("title").value.toString()
                            if (s1 == s2) {
                                chip.setChecked(true)
                            }
                            chip.setText(it2.child(counter.toString()).child("title").value.toString())
                            view.addView(chip)

                            chips.add(counter, chip)
                            chipsID.add(counter, it2.child(counter.toString()).child("id").value.toString())
                            counter += 1
                        }

                        // Get image
                        val path = "images/" + globalVars.Companion.userID.toString() + ".jpg"
                        val storageRef = storage.reference.child(path)
                        storageRef.downloadUrl.addOnSuccessListener { Uri->
                            Glide.with(this)
                                .load(Uri.toString())
                                .into(binding.profilePic)
                        }
                    }
                }

            }
        }


        // Image button
        binding.profilePic.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, EditProfile.CHOOSE_PHOTO)
        }


        // Save button
        binding.save.setOnClickListener {
            reference.get().addOnSuccessListener {

                if (it.exists()) {
                    reference.child(globalVars.Companion.userID).child("username").setValue(binding.username.text.toString())

                    reference.child(globalVars.Companion.userID).child("genres").setValue("")
                    var counter = 0
                    var entryCounter = 0
                    while (counter < size) {
                        if (chips[counter].isChecked) {
                            reference.child(globalVars.Companion.userID).child("genres").child(counter.toString()).child("title").setValue(chips[counter].text)
//                            reference.child(globalVars.Companion.userID).child("genres").child(counter.toString()).child("id").setValue(chipsID[counter])
                            entryCounter += 1
                        }
                        counter += 1
                    }

                    // Save image
                    val path = "images/" + globalVars.Companion.userID.toString() + ".jpg"
                    val storageRef = storage.reference.child(path)
                    val bitmap = (binding.profilePic.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    storageRef.putBytes(data).addOnFailureListener {
                        Toast.makeText(this, "Unable to update profile", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Unable to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }


//        return binding.root


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == EditProfile.CHOOSE_PHOTO){
            var img: ImageView = binding.profilePic
            img.setImageURI(data?.data)
        }
    }

    companion object {
        private val CHOOSE_PHOTO = 2
    }
}