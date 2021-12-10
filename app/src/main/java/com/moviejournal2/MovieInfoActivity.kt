package com.moviejournal2

import MoviesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.MoviesRepository.getRecommendedMovies
import com.moviejournal2.fragments.JournalFragment
import java.text.SimpleDateFormat

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

    // Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var recommendedMoviesPage = 1
    private var id: Int = 0

    private lateinit var watchlistButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var journalButton: ImageButton

    private val db: AppDB by lazy{
        Room.databaseBuilder(
            applicationContext,
            AppDB::class.java,
            "movies.db"
        ).allowMainThreadQueries().build()
    }

    private val db2: AppDB by lazy{
        Room.databaseBuilder(
            applicationContext,
            AppDB::class.java,
            "movies2.db"
        ).allowMainThreadQueries().build()
    }

    //This can return a null MovieUnit if the movie doesnt exist in the db
    private fun getMovie(id: Long): MovieUnit?{
        return db.daoMovie().findById(id)
    }

    private fun getMovie2(id: Long): MovieUnit?{
        return db2.daoMovie().findById(id)
    }


    private var movieId = 0L
    private var movieBackdrop = ""
    private var moviePoster = ""
    private var movieTitle = ""
    private var movieRating = 0f
    private var movieReleaseDate = ""
    private var movieOverview = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")

        backdrop = findViewById(R.id.backdrop)
        poster = findViewById(R.id.movie_poster)
        title = findViewById(R.id.movie_title)
        rating = findViewById(R.id.movie_rating)
        releaseDate = findViewById(R.id.movie_release_date)
        overview = findViewById(R.id.movie_overview)

        val extras = intent.extras
        watchlistButton = findViewById(R.id.watchlistBtn)
        likeButton = findViewById(R.id.likeBtn)
        journalButton = findViewById(R.id.journalBtn)

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

    override fun onStart(){
        super.onStart()

        watchlistButton.setOnClickListener{
            if (getMovie(movieId) == null){
                val entity = MovieUnit(
                    movieId,
                    movieTitle,
                    movieOverview,
                    moviePoster,
                    movieBackdrop,
                    movieRating,
                    movieReleaseDate
                )
                db.daoMovie().insert(entity)
                watchlistButton.setBackgroundResource(R.drawable.add)
                Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT).show()
            }
            else{
                db.daoMovie().delete(movieId)
                watchlistButton.setBackgroundResource(R.drawable.remove)
                Toast.makeText(this, "Movie removed from watchlist", Toast.LENGTH_SHORT).show()
            }
        }

        likeButton.setOnClickListener{
            if (getMovie2(movieId) == null){
                val entity = MovieUnit(
                    movieId,
                    movieTitle,
                    movieOverview,
                    moviePoster,
                    movieBackdrop,
                    movieRating,
                    movieReleaseDate
                )
                db2.daoMovie().insert(entity)
                likeButton.setBackgroundResource(R.drawable.heart)
                Toast.makeText(this, "Movie liked", Toast.LENGTH_SHORT).show()
            }
            else{
                db2.daoMovie().delete(movieId)
                likeButton.setBackgroundResource(R.drawable.brokenheart)
                Toast.makeText(this, "Movie unliked", Toast.LENGTH_SHORT).show()
            }
        }

        journalButton.setOnClickListener{
            if (movieTitle != "") {
                val i = Intent(this, MainActivity::class.java)
                i.putExtra("movie", movieTitle)
                i.putExtra("fragment", 1)
                startActivity(i)
            }
        }
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
        movieId = extras.getInt(MOVIE_ID).toLong()
        movieBackdrop = extras.getString(MOVIE_BACKDROP, "")
        moviePoster = extras.getString(MOVIE_POSTER, "")
        movieTitle = extras.getString(MOVIE_TITLE, "")
        movieRating = extras.getFloat(MOVIE_RATING, 0f)
        movieReleaseDate = extras.getString(MOVIE_RELEASE_DATE, "")
        movieOverview = extras.getString(MOVIE_OVERVIEW, "")

        title.text = movieTitle
        rating.rating = movieRating / 2
        releaseDate.text = movieReleaseDate
        overview.text = movieOverview

        val movie = getMovie(movieId)

        if(movie == null){
            watchlistButton.setBackgroundResource(R.drawable.add)
        }else{
            watchlistButton.setBackgroundResource(R.drawable.remove)
        }
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