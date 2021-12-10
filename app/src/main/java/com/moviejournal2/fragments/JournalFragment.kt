package com.moviejournal2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.*
import com.moviejournal2.databinding.FragmentJournalBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    private var movie: Movie? = null
    private var gotMovie = false

    private fun failure() {
        Toast.makeText(context, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun success(movies: List<Movie>) {
        movie = movies[0]
        gotMovie = true
    }

    suspend fun getMovie(s: String) = coroutineScope {
        launch {
            MoviesRepository.getRequestedMovie(1, s,
                ::success, ::failure)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentJournalBinding.inflate(layoutInflater)

        val newEntry = activity?.intent?.extras?.getString("movie")
        if (newEntry == null) {
            binding.newEntry.visibility = GONE
        } else {
            binding.newEntryText.visibility = GONE
        }


        val calendar = CalendarView(requireContext())
        binding.calendar.addView(calendar)

        // Get database date for automatic date
        getEntries(calendar.date)


        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val c: Calendar = Calendar.getInstance()
            c.set(year,month,dayOfMonth)
            calendar.date = c.timeInMillis

            // Get entries on selected date
            getEntries(calendar.date)
        }


        // New entry button
        binding.newEntry.setOnClickListener {
            val i = Intent(requireContext(), EditJournalEntry::class.java)
            val savedate = SimpleDateFormat("yyyy/MM/dd").format(calendar.date)
            var index = 0
            reference.child(globalVars.Companion.userID).child("journal")
            .child(savedate).get().addOnSuccessListener { d->
                if (d.exists()) {
                    while (index < d.childrenCount) {
                        if (d.child(index.toString()).child("movie").exists() == false) {
                            break
                        }
                        index += 1
                    }
                }
                i.putExtra("id", index)
                i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(calendar.date))
                i.putExtra("savedate", savedate)
                i.putExtra("new", true)
                if (activity?.intent?.extras?.getString("movie") != null) {
                    i.putExtra("movie", activity?.intent?.extras?.getString("movie"))
                }
                startActivity(i)
            }
        }

        return binding.root
    }

    fun getEntries(d: Long)  = runBlocking {
        val savedate = SimpleDateFormat("yyyy/MM/dd").format(d)

        // Iterate through journal entries for that day
        binding.entries.removeAllViews()
        reference.child(globalVars.Companion.userID).child("journal")
            .child(savedate).get().addOnSuccessListener {
                if (it.exists()) {
                    var counter = 1
                    it.children.forEach { c->
                        // Get movie
                        val t = it.child(c.key.toString()).child("movie").value.toString()
                        launch {
                            getMovie(t)
                        }

                        val e = LinearLayout(context)
                        e.layoutParams = binding.entry.layoutParams
                        e.gravity = Gravity.CENTER_HORIZONTAL
                        e.orientation = LinearLayout.VERTICAL

                        val title = TextView(context)
                        title.layoutParams = binding.title.layoutParams
                        title.setText("Journal entry "+counter)
                        title.textSize = 15.toFloat()
                        e.addView(title)

                        // Onclicklistener for journal entry
                        e.setOnClickListener { a->
                            val i = Intent(requireContext(), EditJournalEntry::class.java)
                            i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(d))
                            i.putExtra("savedate", savedate)
                            i.putExtra("new", false)
                            i.putExtra("movie", movie?.title)
                            i.putExtra("id", c.key)
                            startActivity(i)
                        }

                        val content = LinearLayout(context)
                        content.layoutParams = binding.content.layoutParams
                        content.gravity = Gravity.CENTER_HORIZONTAL

                        val img = ImageView(context)
                        img.layoutParams = binding.entryImg.layoutParams
                        Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w342${movie?.posterPath}")
                            .into(img)
                        content.addView(img)

                        val text = TextView(context)
                        text.layoutParams = binding.text.layoutParams
                        text.setText(it.child(c.key.toString()).child("text").value.toString())
                        content.addView(text)

                        content.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                        e.addView(content)

                        binding.entries.addView(e)
                        counter += 1
                    }
                }
            }
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