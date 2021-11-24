package com.moviejournal2.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        // Chip array
        var size = 0
        var counter = 0
        var chips = List(size) { Chip(getActivity()) }.toMutableList()


        // Get database data
        reference.child(globalVars.Companion.userID).get().addOnSuccessListener {
            if (it.exists()) {
                binding.username.text = it.child("username").value.toString().toEditable()
//                binding.favmovie.text = it.child("favmovie").value.toString().toEditable()
//                binding.chip.text = it.child("genres").value.toString()

                database.getReference("genres").get().addOnSuccessListener { it2: DataSnapshot ->
                    if (it2.exists()) {
                        Toast.makeText(activity, "Genres found", Toast.LENGTH_SHORT).show()
                        size = it2.childrenCount.toInt()
                        counter = 0
                        var view: LinearLayout = binding.chips

                        while (counter < size) {
                            var chip = Chip(getActivity())
                            chip.setCheckable(true)
                            chip.setText(it2.child((counter.toString())).value.toString())
                            view.addView(chip)

                            chips.add(counter, chip)
                            counter += 1
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
                    Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()

                    reference.child(globalVars.Companion.userID).child("genres").setValue("")
                    var counter = 0
                    var entryCounter = 0
//                    Log.d("test", "asd")
//                    Log.d("size", size.toString())
//                    Log.d("counter", counter.toString())
                    while (counter < size) {
//                        Log.d("test", chips[counter].checkedIcon.toString())
//                        Log.d("test", chips[counter].text.toString())
//                        Log.d("test", chips[counter].isChecked.toString())
                        if (chips[counter].isChecked) {
//                            reference.child(globalVars.Companion.userID).child("genres").child(entryCounter.toString()).setValue(counter.toString())
                            reference.child(globalVars.Companion.userID).child("genres").child(entryCounter.toString()).setValue(chips[counter].text)
                            entryCounter += 1
                        }

                        counter += 1
                    }

                } else {
                    Toast.makeText(activity, "Unable to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Inflate the layout for this fragment
        return binding.root
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
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}