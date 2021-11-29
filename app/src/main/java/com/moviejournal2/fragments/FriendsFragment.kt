package com.moviejournal2.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        reference.child(globalVars.Companion.userID).child("friends").get().addOnSuccessListener {
            if (it.exists()) {
                var view: LinearLayout = binding.friends

                // Find users by username
                it.children.forEach { u: DataSnapshot ->
                    var card = TextView(getActivity())

                    reference.child(u.value.toString()).get().addOnSuccessListener { it2: DataSnapshot ->
                        if (it2.exists()) {
                            card.setText(it2.child("username").value.toString())
                        }
                    }

                    card.layoutParams = binding.friendCard.layoutParams
                    view.addView(card)
                }


//                var counter = 0
//                while (counter < 3) {
////                    var card = TextView(getActivity())
//                    var card = TextView(getActivity())
//                    var temp = "test" + counter.toString()
//                    card.setText(temp)
//
//                    card.layoutParams = binding.friendCard.layoutParams
//                    view.addView(card)
//
//                    counter += 1
//                }
            }
        }


        // Search button
        binding.searchButton.setOnClickListener {
            Toast.makeText(activity, "Search", Toast.LENGTH_SHORT).show()

            reference.get().addOnSuccessListener {
                if (it.exists()) {
                    var view: LinearLayout = binding.friends

                    // Loop through all users
                    it.children.forEach { u: DataSnapshot ->
                        var temp = TextView(getActivity())
                        temp.setText(u.child("username").value.toString())

                        temp.layoutParams = binding.friendCard.layoutParams
                        view.addView(temp)
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