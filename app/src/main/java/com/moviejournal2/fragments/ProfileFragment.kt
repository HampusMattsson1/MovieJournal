package com.moviejournal2.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.moviejournal2.R
import com.moviejournal2.databinding.FragmentProfileBinding
import com.moviejournal2.sign_in
import androidx.databinding.DataBindingUtil.setContentView as setContentView1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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
    private lateinit var binding: FragmentProfileBinding

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
//        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_profile)

        binding.save.setOnClickListener {

            reference.get().addOnSuccessListener {

                Toast.makeText(activity, "Profile saved", Toast.LENGTH_SHORT).show()

                if (it.exists()) {
                    val m = it.child("favmovie").value
                    Log.d("test", "test")
                    binding.favMovie.text = m.toString().toEditable()
                    binding.chip.text = m.toString()


                } else {
                    Toast.makeText(activity, "Fail", Toast.LENGTH_SHORT).show()
                }
            }


        }

//        var button = Button(activity)
//        button = v.findViewById<Button>(R.id.save)
//        button.setOnClickListener {
////            Toast.makeText(activity,"Profile saved", Toast.LENGTH_SHORT).show()
//
//            reference.child("test").get().addOnSuccessListener {
//
//                if (it.exists()) {
//                    val m = it.child("favMovie").value
//                    if (m != null) {
//                        binding.test.text = m.toString()
//                    }
//
//
//                } else {
//                    Toast.makeText(activity,"Fail", Toast.LENGTH_SHORT).show()
//                }
//
//
//            }
//        }

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}