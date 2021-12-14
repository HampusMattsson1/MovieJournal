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
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    private var movies: MutableList<Movie> = ArrayList()
    private var text: MutableList<String> = ArrayList()
    private var index: MutableList<String> = ArrayList()

    private fun failure() {
        Toast.makeText(context, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun success(m: List<Movie>) {
        movies.add(m[0])
    }


    fun getMovies(s: List<String>) {
        s.forEach {
            MoviesRepository.getRequestedMovie(1, it,
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
        if (newEntry == null || newEntry == "") {
            binding.newEntry.visibility = GONE
        } else {
            binding.newEntryText.visibility = GONE
        }


        val calendar = CalendarView(requireContext())
        binding.calendar.addView(calendar)

        // Get database date for automatic date
        if (newEntry == null) {
            GlobalScope.launch(Dispatchers.IO) {
                val rec: suspend CoroutineScope.() -> Unit =  {
                    withContext(Dispatchers.Main) { //Suspension functions can be called only within coroutine body
//                        getEntries(calendar.date)
                        movies.clear()
                        text.clear()
                        index.clear()
                        getData(calendar.date)
                    }
                }
                foo(this,rec)
            }
        }


        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val c: Calendar = Calendar.getInstance()
            c.set(year,month,dayOfMonth)
            calendar.date = c.timeInMillis

            // Get entries on selected date
            if (newEntry == null) {
                GlobalScope.launch(Dispatchers.IO) {
                    val rec: suspend CoroutineScope.() -> Unit =  {
                        withContext(Dispatchers.Main) {
//                            getEntries(calendar.date)
                            movies.clear()
                            text.clear()
                            index.clear()
                            getData(calendar.date)
                        }
                    }
                    foo(this,rec)
                }
            }
        }

        binding.showEntries.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val rec: suspend CoroutineScope.() -> Unit =  {
                    withContext(Dispatchers.Main) {
                        showData(calendar.date)
                    }
                }
                foo(this,rec)
            }
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
                i.putExtra("id", index.toString())
                i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(calendar.date))
                i.putExtra("savedate", savedate)
                i.putExtra("new", true)
                if (activity?.intent?.extras?.getString("movie") != null) {
                    i.putExtra("movie", activity?.intent?.extras?.getString("movie"))
                }
                requireActivity().intent!!.removeExtra("movie")
                startActivity(i)
            }
        }

        return binding.root
    }

//    fun getEntries(d: Long) {
//        val savedate = SimpleDateFormat("yyyy/MM/dd").format(d)
//
//        // Iterate through journal entries for that day
//        binding.entries.removeAllViews()
//        reference.child(globalVars.Companion.userID).child("journal")
//        .child(savedate).get().addOnSuccessListener {
//            if (it.exists()) {
//                var m: MutableList<String> = ArrayList()
//
//                it.children.forEach { c ->
//                    val t = it.child(c.key.toString()).child("movie").value.toString()
//                    m.add(t)
//                }
//
//                GlobalScope.launch(Dispatchers.IO) {
//                    val rec: suspend CoroutineScope.() -> Unit =  {
//                        withContext(Dispatchers.Main) { //Suspension functions can be called only within coroutine body
//                            getMovies(m)
//                        }
//                    }
//                    foo(this,rec)
//                }
//
//
//                it.children.forEach { c ->
//                    text.add(it.child(c.key.toString()).child("text").value.toString())
//                    index.add(c.key.toString())
//                }
//
//                var counter = 0
//                movies.forEach { c ->
//                    val e = LinearLayout(context)
//                    e.layoutParams = binding.entry.layoutParams
//                    e.gravity = Gravity.CENTER_HORIZONTAL
//                    e.orientation = LinearLayout.VERTICAL
//
//                    val title = TextView(context)
//                    title.layoutParams = binding.title.layoutParams
//                    title.setText("Journal entry "+(counter+1).toString())
//                    title.textSize = 15.toFloat()
//                    e.addView(title)
//
//                    // Onclicklistener for journal entry
//                    e.setOnClickListener { a->
//                        val i = Intent(requireContext(), EditJournalEntry::class.java)
//                        i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(d))
//                        i.putExtra("savedate", savedate)
//                        i.putExtra("new", false)
//                        i.putExtra("movie", c.title)
//                        i.putExtra("id", index[counter])
////                            Toast.makeText(context, index.elementAt(c.id), Toast.LENGTH_SHORT).show()
//                        startActivity(i)
//                    }
//
//                    val content = LinearLayout(context)
//                    content.layoutParams = binding.content.layoutParams
//                    content.gravity = Gravity.CENTER_HORIZONTAL
//
//                    val img = ImageView(context)
//                    img.layoutParams = binding.entryImg.layoutParams
//                    Glide.with(requireContext())
//                        .load("https://image.tmdb.org/t/p/w342${c.posterPath}")
//                        .into(img)
//                    content.addView(img)
//
//                    val t = TextView(context)
//                    t.layoutParams = binding.text.layoutParams
//                    t.setText(text[counter])
//                    content.addView(t)
//
//                    content.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
//                    e.addView(content)
//
//                    binding.entries.addView(e)
//                    counter += 1
//                }
//            }
//        }
//    }

    suspend fun getData(d: Long) {
        val savedate = SimpleDateFormat("yyyy/MM/dd").format(d)

        // Iterate through journal entries for that day
        binding.entries.removeAllViews()
        reference.child(globalVars.Companion.userID).child("journal")
        .child(savedate).get().addOnSuccessListener {
            if (it.exists()) {
                var m: MutableList<String> = ArrayList()

                it.children.forEach { c ->
                    val t = it.child(c.key.toString()).child("movie").value.toString()
                    m.add(t)
                    text.add(it.child(c.key.toString()).child("quote").value.toString())
                    index.add(c.key.toString())
                }

                GlobalScope.launch(Dispatchers.IO) {
                    val rec: suspend CoroutineScope.() -> Unit =  {
                        withContext(Dispatchers.Main) { //Suspension functions can be called only within coroutine body
                            getMovies(m)
                        }
                    }
                    foo(this,rec)
                }

            }
        }
    }

    fun showData(d: Long) {
        val savedate = SimpleDateFormat("yyyy/MM/dd").format(d)

        // Iterate through journal entries for that day
        binding.entries.removeAllViews()
            var counter = 0
            movies.zip(index).forEach { pair ->
                val e = LinearLayout(context)
                e.layoutParams = binding.entry.layoutParams
                e.gravity = Gravity.CENTER_HORIZONTAL
                e.orientation = LinearLayout.VERTICAL

                val title = TextView(context)
                title.layoutParams = binding.title.layoutParams
                title.setText("Journal entry "+(counter+1).toString())
                title.textSize = 15.toFloat()
                e.addView(title)

                // Onclicklistener for journal entry
                e.setOnClickListener { a->
                    val i = Intent(requireContext(), EditJournalEntry::class.java)
                    i.putExtra("date", SimpleDateFormat("dd/MM/yyyy").format(d))
                    i.putExtra("savedate", savedate)
                    i.putExtra("new", false)
                    i.putExtra("movie", pair.component1().title)
                    i.putExtra("id", pair.component2())
//                            Toast.makeText(context, index.elementAt(c.id), Toast.LENGTH_SHORT).show()
                    startActivity(i)
                }

                val content = LinearLayout(context)
                content.layoutParams = binding.content.layoutParams
                content.gravity = Gravity.CENTER_HORIZONTAL

                val img = ImageView(context)
                img.layoutParams = binding.entryImg.layoutParams
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w342${pair.component1().posterPath}")
                    .into(img)
                content.addView(img)

                val t = TextView(context)
                t.layoutParams = binding.text.layoutParams
                if (text[counter] != "null") {
                    t.setText(text[counter])
                } else {
                    t.setText(" ")
                }
                content.addView(t)

                content.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
                e.addView(content)

                binding.entries.addView(e)
                counter += 1
            }
        }





    private suspend fun foo( coroutineScope: CoroutineScope , lambda : suspend CoroutineScope.() -> Unit)  {
        lambda.invoke(coroutineScope)
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