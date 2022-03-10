package com.moviejournal2.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks.await
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.*
import com.moviejournal2.databinding.FragmentProfileBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.CompletableFuture.runAsync
import kotlin.Result.Companion.failure
import kotlin.coroutines.coroutineContext

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
    private lateinit var storage: FirebaseStorage

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private lateinit var likedList: RecyclerView
    private lateinit var likedListAdapter: LikedListAdapter
    private lateinit var likedListLayoutManager: LinearLayoutManager

    private val db2: AppDB by lazy{
        Room.databaseBuilder(
            requireActivity().applicationContext,
            AppDB::class.java,
            "movies2.db"
        ).allowMainThreadQueries().build()
    }

    private suspend fun foo( coroutineScope: CoroutineScope , lambda : suspend CoroutineScope.() -> Unit)  {
        lambda.invoke(coroutineScope)
    }

    suspend fun setMovie(s: String) {
        var m: Movie? = null
        coroutineScope {
            m = MoviesRepository.getRequestedMovie2(1, s)
        }

        if (m == null) {
            binding.favmovie.setImageDrawable(R.drawable.profile.toDrawable())
        } else {
            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w342${m!!.posterPath}")
                .into(binding.favmovie)
        }
    }

    fun setFavMovie(m: String?) {
        if (m != null) {
            Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w342${m}")
            .into(binding.favmovie)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = FragmentProfileBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()

        likedList = binding.likedlist
        likedListLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        likedList.layoutManager = likedListLayoutManager
        likedListAdapter = LikedListAdapter(listOf()) {
            when (it.type) {
                is LikedListType.MovieType -> showMovieDetailsLikedlist(it)
            }
        }
        likedList.adapter = likedListAdapter
        getLikedList()


        // Get database data
        reference.child(globalVars.Companion.userID).get().addOnSuccessListener {
            if (it.exists()) {
                binding.user.text = it.child("username").value.toString()

                // Favourite movie
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w342/${it.child("favmovie").value.toString()}")
                    .into(binding.favmovie)

                // Chips
                reference.child(globalVars.Companion.userID).child("genres").get().addOnSuccessListener { it2: DataSnapshot ->
                    if (it2.exists()) {
//                        val size = it2.childrenCount.toInt()
                        var counter = 0
                        var view: ChipGroup = binding.chipGroup

                        while (counter < 20) {
                            if (it2.child(counter.toString()).value != null) {
                                var chip = Chip(context)
                                chip.isCheckable = false
                                chip.text = it2.child(counter.toString()).child("title").value.toString()
                                view.addView(chip)
                            }
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


        // Settings button
        binding.settings.setOnClickListener {
//            (activity as MainActivity).replaceFragment(EditProfileFragment())
            val i = Intent(requireContext(), EditProfile::class.java)
            startActivity(i)
        }

        return binding.root
    }

    private fun showMovieDetailsLikedlist(item: LikedList){
        val intent = Intent(requireActivity(), MovieInfoActivity::class.java)
        intent.putExtra(MOVIE_ID, item.id)
        intent.putExtra(MOVIE_BACKDROP, item.backdropPath)
        intent.putExtra(MOVIE_POSTER, item.posterPath)
        intent.putExtra(MOVIE_TITLE, item.title)
        intent.putExtra(MOVIE_RATING, item.rating)
        intent.putExtra(MOVIE_RELEASE_DATE, item.releaseDate)
        intent.putExtra(MOVIE_OVERVIEW, item.overview)
        startActivity(intent)
    }

    private fun getLikedList() {
        val movies = db2.daoMovie().getAll()

        val likedList = mutableListOf<LikedList>()

        likedList.addAll(
            movies.map { movie ->
                LikedList(
                    movie.id,
                    movie.title,
                    movie.overview,
                    movie.posterPath,
                    movie.backdropPath,
                    movie.rating,
                    movie.releaseDate,
                    LikedListType.MovieType
                )
            }
        )

        likedListAdapter.updateItems(likedList)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden){
            getLikedList()
        }
    }

    override fun onResume() {
        super.onResume()
        getLikedList()
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