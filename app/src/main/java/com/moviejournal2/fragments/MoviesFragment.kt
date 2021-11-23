package com.moviejournal2.fragments

import MoviesAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moviejournal2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoviesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoviesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Popular movies
    private lateinit var popularMovies: RecyclerView
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularMoviesLayoutManager: LinearLayoutManager

    private var popularMoviesPage = 1

    private fun getPopularMovies(){
        MoviesRepository.getPopularMovies(
            popularMoviesPage,
            ::onPopularMoviesFetched,
            ::onError
        )
    }

    private fun attachPopularMoviesOnScrollListener(){
        popularMovies.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                //total nbr of movies inside of popularMoviesAdapter
                val totalItemCount = popularMoviesLayoutManager.itemCount
                //current nb of child views attached to recyclerview
                val visibleItemCount = popularMoviesLayoutManager.childCount
                //position of the first visible item in list
                val firstVisibleItem = popularMoviesLayoutManager.findFirstVisibleItemPosition()

                //if the user has scrolled past halfway + 1 buffered value of visibleItemCount
                if(firstVisibleItem + visibleItemCount >= totalItemCount/2){
                    //scroll listener disabled
                    popularMovies.removeOnScrollListener(this)
                    popularMoviesPage++
                    getPopularMovies()
                }
            }
        })
    }

    private fun onPopularMoviesFetched(movies: List<Movie>){
        popularMoviesAdapter.appendMovies(movies)
        attachPopularMoviesOnScrollListener()
    }

    //Top rated movies
    private lateinit var topRatedMovies: RecyclerView
    private lateinit var topRatedMoviesAdapter: MoviesAdapter
    private lateinit var topRatedMoviesLayoutManager: LinearLayoutManager

    private var topRatedMoviesPage = 1

    private fun getTopRatedMovies(){
        MoviesRepository.getTopRatedMovies(
            topRatedMoviesPage,
            ::onTopRatedMoviesFetched,
            ::onError
        )
    }

    private fun attachTopRatedMoviesOnScrollListener(){
        topRatedMovies.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                //total nbr of movies inside of topRatedMoviesAdapter
                val totalItemCount = topRatedMoviesLayoutManager.itemCount
                //current nb of child views attached to recyclerview
                val visibleItemCount = topRatedMoviesLayoutManager.childCount
                //position of the first visible item in list
                val firstVisibleItem = topRatedMoviesLayoutManager.findFirstVisibleItemPosition()

                //if the user has scrolled past halfway + 1 buffered value of visibleItemCount
                if(firstVisibleItem + visibleItemCount >= totalItemCount/2){
                    //scroll listener disabled
                    topRatedMovies.removeOnScrollListener(this)
                    topRatedMoviesPage++
                    getTopRatedMovies()
                }
            }
        })
    }

    private fun onTopRatedMoviesFetched(movies: List<Movie>){
        topRatedMoviesAdapter.appendMovies(movies)
        attachTopRatedMoviesOnScrollListener()
    }

    //Upcoming movies

    private lateinit var upcomingMovies: RecyclerView
    private lateinit var upcomingMoviesAdapter: MoviesAdapter
    private lateinit var upcomingMoviesLayoutManager: LinearLayoutManager

    private var upcomingMoviesPage = 1

    private fun getUpcomingMovies(){
        MoviesRepository.getUpcomingMovies(
            upcomingMoviesPage,
            ::onUpcomingMoviesFetched,
            ::onError
        )
    }

    private fun attachUpcomingMoviesOnScrollListener(){
        upcomingMovies.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                //total nbr of movies inside of topRatedMoviesAdapter
                val totalItemCount = upcomingMoviesLayoutManager.itemCount
                //current nb of child views attached to recyclerview
                val visibleItemCount = upcomingMoviesLayoutManager.childCount
                //position of the first visible item in list
                val firstVisibleItem = upcomingMoviesLayoutManager.findFirstVisibleItemPosition()

                //if the user has scrolled past halfway + 1 buffered value of visibleItemCount
                if(firstVisibleItem + visibleItemCount >= totalItemCount/2){
                    //scroll listener disabled
                    upcomingMovies.removeOnScrollListener(this)
                    upcomingMoviesPage++
                    getUpcomingMovies()
                }
            }
        })
    }

    private fun onUpcomingMoviesFetched(movies: List<Movie>){
        upcomingMoviesAdapter.appendMovies(movies)
        attachUpcomingMoviesOnScrollListener()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private fun onError(){
        Toast.makeText(requireActivity(), getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun showMovieDetails(movie: Movie){
        val intent = Intent(requireActivity(), MovieInfoActivity::class.java)
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
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_movies, container, false)
        // Inflate the layout for this fragment
        popularMovies = rootView.findViewById<RecyclerView>(R.id.popular_movies)
        topRatedMovies = rootView.findViewById<RecyclerView>(R.id.top_rated_movies)
        upcomingMovies = rootView.findViewById<RecyclerView>(R.id.upcoming_movies)
        popularMoviesLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        topRatedMoviesLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        upcomingMoviesLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        popularMovies.layoutManager = popularMoviesLayoutManager
        popularMoviesAdapter = MoviesAdapter(mutableListOf()){
            movie -> showMovieDetails(movie)
        }
        popularMovies.adapter = popularMoviesAdapter

        topRatedMovies.layoutManager = topRatedMoviesLayoutManager
        topRatedMoviesAdapter = MoviesAdapter(mutableListOf()){
                movie -> showMovieDetails(movie)
        }
        topRatedMovies.adapter = topRatedMoviesAdapter

        upcomingMovies.layoutManager = upcomingMoviesLayoutManager
        upcomingMoviesAdapter = MoviesAdapter(mutableListOf()){
                movie -> showMovieDetails(movie)
        }
        upcomingMovies.adapter = upcomingMoviesAdapter

        getPopularMovies()
        getTopRatedMovies()
        getUpcomingMovies()

        return rootView
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoviesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoviesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}