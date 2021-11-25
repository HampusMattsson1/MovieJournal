package com.moviejournal2

import MoviesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.moviejournal2.MoviesRepository.getRecommendedMovies

const val MOVIE_ID = "extra_movie_id"
const val MOVIE_BACKDROP = "extra_movie_backdrop"
const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_RATING = "extra_movie_rating"
const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
const val MOVIE_OVERVIEW = "extra_movie_overview"

class MovieInfoActivity : AppCompatActivity() {

    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView

    //for recommended movies
    private lateinit var recommendedMovies: RecyclerView
    private lateinit var recommendedMoviesAdapter: MoviesAdapter
    private lateinit var recommendedMoviesLayoutManager: LinearLayoutManager

    private var recommendedMoviesPage = 1
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)

        backdrop = findViewById(R.id.backdrop)
        poster = findViewById(R.id.movie_poster)
        title = findViewById(R.id.movie_title)
        rating = findViewById(R.id.movie_rating)
        releaseDate = findViewById(R.id.movie_release_date)
        overview = findViewById(R.id.movie_overview)

        val extras = intent.extras

        if(extras != null){
            id = extras.getInt(MOVIE_ID, 0)
            Log.i("MovieInfoActivity", "$id")
            fillDetails(extras)
        }else{
            finish()
        }

        recommendedMovies = findViewById(R.id.recommended_movies)
        recommendedMoviesLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recommendedMovies.layoutManager = recommendedMoviesLayoutManager
        recommendedMoviesAdapter = MoviesAdapter(mutableListOf()){ movie -> showMovieDetails(movie) }
        recommendedMovies.adapter = recommendedMoviesAdapter

        getRecommendedMovies()
    }

    private fun fillDetails(extras: Bundle){
        extras.getString(MOVIE_BACKDROP)?.let{ backdropPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280$backdropPath")
                .transform(CenterCrop())
                .into(backdrop)
        }

        extras.getString(MOVIE_POSTER)?.let{ posterPath ->
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342$posterPath")
                .transform(CenterCrop())
                .into(poster)
        }
        title.text = extras.getString(MOVIE_TITLE, "")
        rating.rating = extras.getFloat(MOVIE_RATING, 0f) / 2
        releaseDate.text = extras.getString(MOVIE_RELEASE_DATE, "")
        overview.text = extras.getString(MOVIE_OVERVIEW, "")
    }

    private fun getRecommendedMovies() {
        Log.i("getRecommendedMovies", "$id")
        MoviesRepository.getRecommendedMovies(
            id,
            recommendedMoviesPage,
            ::onRecommendedMoviesFetched,
            ::onError
        )
    }

    private fun attachRecommendedMoviesOnScrollListener() {
        recommendedMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = recommendedMoviesLayoutManager.itemCount
                val visibleItemCount = recommendedMoviesLayoutManager.childCount
                val firstVisibleItem = recommendedMoviesLayoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    recommendedMovies.removeOnScrollListener(this)
                    recommendedMoviesPage++
                    getRecommendedMovies()
                }
            }
        })
    }

    private fun onRecommendedMoviesFetched(movies: List<Movie>) {
        recommendedMoviesAdapter.appendMovies(movies)
        attachRecommendedMoviesOnScrollListener()
        Log.i("MoviesFetched", "$movies")
    }

    private fun onError(){
        Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun showMovieDetails(movie: Movie) {
        val intent = Intent(this, MovieInfoActivity::class.java)
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



}