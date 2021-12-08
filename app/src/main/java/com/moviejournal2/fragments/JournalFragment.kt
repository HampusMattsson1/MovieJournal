package com.moviejournal2.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.EditJournalEntry
import com.moviejournal2.EditProfile
import com.moviejournal2.R
import com.moviejournal2.databinding.FragmentJournalBinding
import com.moviejournal2.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JournalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalFragment : Fragment() {
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
    private lateinit var binding: FragmentJournalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentJournalBinding.inflate(layoutInflater)


        val calendar = CalendarView(requireContext())

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val c: Calendar = Calendar.getInstance()
            c.set(year,month,dayOfMonth)
            calendar.date = c.timeInMillis
        }


        // New entry button
        binding.newEntry.setOnClickListener {
            Toast.makeText(requireContext(), SimpleDateFormat("dd/MM/yyyy").format(calendar.date), Toast.LENGTH_SHORT).show()
            val i = Intent(requireContext(), EditJournalEntry::class.java)
            i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(calendar.date))
            startActivity(i)
        }

        binding.calendar.addView(calendar)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JournalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JournalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}