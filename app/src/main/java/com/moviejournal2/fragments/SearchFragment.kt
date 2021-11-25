package com.moviejournal2.fragments

import MoviesAdapter
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.moviejournal2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Results for requested movie
    private lateinit var requestedMovie: RecyclerView
    private lateinit var requestedMovieAdapter: MoviesAdapter
    private lateinit var requestedMovieLayoutManager: LinearLayoutManager

    private lateinit var requestedMovieName: String

    private var requestedMoviePage = 1

    private fun getRequestedMovie(){
        MoviesRepository.getRequestedMovie(
            requestedMoviePage,
            requestedMovieName,
            ::onRequestedMovieFetched,
            ::onError
        )
    }

    private fun attachRequestedMovieOnScrollListener(){
        requestedMovie.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                //total nbr of movies inside of popularMoviesAdapter
                val totalItemCount = requestedMovieLayoutManager.itemCount
                //current nb of child views attached to recyclerview
                val visibleItemCount = requestedMovieLayoutManager.childCount
                //position of the first visible item in list
                val firstVisibleItem = requestedMovieLayoutManager.findFirstVisibleItemPosition()

                //if the user has scrolled past halfway + 1 buffered value of visibleItemCount
                if(firstVisibleItem + visibleItemCount >= totalItemCount/2){
                    //scroll listener disabled
                    requestedMovie.removeOnScrollListener(this)
                    requestedMoviePage++
                    getRequestedMovie()
                }
            }
        })
    }

    private fun onRequestedMovieFetched(movies: List<Movie>){
        requestedMovieAdapter.appendMovies(movies)
        attachRequestedMovieOnScrollListener()
    }

    private fun onError(){
        Toast.makeText(requireActivity(), getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun showMovieDetails(movie: Movie){
        val intent = Intent(requireActivity(), MovieInfoActivity::class.java)
        intent.putExtra(MOVIE_ID, movie.id)
        Log.i("showMovieDetails","${movie.id}")
        intent.putExtra(MOVIE_BACKDROP, movie.backdropPath)
        intent.putExtra(MOVIE_POSTER, movie.posterPath)
        intent.putExtra(MOVIE_TITLE, movie.title)
        intent.putExtra(MOVIE_RATING, movie.rating)
        intent.putExtra(MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MOVIE_OVERVIEW, movie.overview)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)


        val msearch = rootView.findViewById<TextInputEditText>(R.id.textEditSearch)
        val searchButton = rootView.findViewById<Button>(R.id.btn_search)
        if (searchButton != null && msearch != null){
            searchButton.setOnClickListener{
                when{
                    TextUtils.isEmpty(msearch.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(
                            requireActivity(),
                            "Please enter a movie name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                requestedMovie = rootView.findViewById<RecyclerView>(R.id.search_results)
                requestedMovieLayoutManager = LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                requestedMovie.layoutManager = requestedMovieLayoutManager
                requestedMovieAdapter = MoviesAdapter(mutableListOf()){
                        movie -> showMovieDetails(movie)
                }
                requestedMovie.adapter = requestedMovieAdapter
                requestedMovieName = msearch.text.toString().trim{it <= ' '}
                getRequestedMovie()
            }
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}