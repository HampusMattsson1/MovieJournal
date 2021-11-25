package com.moviejournal2.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.R
import com.moviejournal2.databinding.FragmentEditProfileBinding
import com.moviejournal2.databinding.FragmentProfileBinding
import com.moviejournal2.globalVars
import com.moviejournal2.MainActivity

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.tasks.OnSuccessListener

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
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
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var storage: FirebaseStorage

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        database =
            FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        // Chip array & variables
        var size = 0
        var counter = 0
        var chips = List(size) { Chip(getActivity()) }.toMutableList()
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
                            var chip = Chip(getActivity())
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
                        val img: Long = 1024 * 1024
                        storageRef.getBytes(img).addOnSuccessListener {
                            Toast.makeText(activity, "image gathered", Toast.LENGTH_SHORT).show()
                            Glide.with(this)
                                .load(storageRef)
                                .into(binding.profilePic)
                        }.addOnFailureListener {
                            Toast.makeText(activity, "Unable to load image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
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
                        Toast.makeText(activity, "Unable to update profile", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "Unable to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Image button
        binding.profilePic.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, CHOOSE_PHOTO)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_PHOTO){
            var img: ImageView = binding.profilePic
            img.setImageURI(data?.data)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic

        private val CHOOSE_PHOTO = 2

        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}