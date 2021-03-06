package com.moviejournal2

import MoviesAdapter
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.MoviesRepository.getRecommendedMovies
import com.moviejournal2.fragments.*
import okhttp3.Interceptor.Companion.invoke
import java.util.*
import kotlin.random.Random
import android.R.id
import android.content.SharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView

open class MainActivity : AppCompatActivity() {
    private val journalFragment = JournalFragment()
    private val friendsFragment = FriendsFragment()
    private val moviesFragment = MoviesFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var reference_liked: DatabaseReference
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager

    private val channelId = "100000"

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
                val intent1 = Intent(context, MainActivity::class.java)
                intent1.putExtra("type", 4)
                }
            }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val b: Bundle? = intent.extras
        val fragment = b?.getInt("fragment")
        if (fragment == 0) {
            replaceFragment(moviesFragment)
        } else {
            replaceFragment(journalFragment)
        }

        val bottom_nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_nav.selectedItemId = R.id.ic_movies
        bottom_nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.ic_movies -> replaceFragment(moviesFragment)
                R.id.ic_journal -> replaceFragment(journalFragment)
                R.id.ic_people -> replaceFragment(friendsFragment)
                R.id.ic_search -> replaceFragment(searchFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }
            true
        }


        fun createAlarm() {
            //System request code
            val DATA_FETCHER_RC = 123
            //Create an alarm manager
            val mAlarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val calendar: Calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, 14)
            calendar.set(Calendar.MINUTE, 15)

            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                DATA_FETCHER_RC,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


            //Also set the interval using the AlarmManager constants
            mAlarmManager.setInexactRepeating(
                AlarmManager.RTC,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }


        createAlarm()


        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users/" + globalVars.userID + "/requests")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        reference_liked = database.getReference("users/" + globalVars.userID + "/likedlist")

        var recommendedMoviesPage = 1

        fun showMovieDetails(movie: Movie) : Intent{
            val intent = Intent(this, MovieInfoActivity::class.java)
            intent.putExtra(MOVIE_ID, movie.id)
            intent.putExtra(MOVIE_BACKDROP, movie.backdropPath)
            intent.putExtra(MOVIE_POSTER, movie.posterPath)
            intent.putExtra(MOVIE_TITLE, movie.title)
            intent.putExtra(MOVIE_RATING, movie.rating)
            intent.putExtra(MOVIE_RELEASE_DATE, movie.releaseDate)
            intent.putExtra(MOVIE_OVERVIEW, movie.overview)
            return intent
        }

        fun showMovieDetailsForNotif(movie: Movie) : Intent{
            val intent = Intent(this, MovieInfoActivityNew::class.java)
            intent.putExtra(MOVIE_ID_2, movie.id)
            intent.putExtra(MOVIE_BACKDROP_2, movie.backdropPath)
            intent.putExtra(MOVIE_POSTER_2, movie.posterPath)
            intent.putExtra(MOVIE_TITLE_2, movie.title)
            intent.putExtra(MOVIE_RATING_2, movie.rating)
            intent.putExtra(MOVIE_RELEASE_DATE_2, movie.releaseDate)
            intent.putExtra(MOVIE_OVERVIEW_2, movie.overview)
            return intent
        }

        fun getRecommendedMovies(id: Int) {

            fun onRecommendedMoviesFetched2(movies: List<Movie>) {
                val randomIndex = Random.nextInt(movies.size)
                //val movieToGetRecommendationsId = movies[randomIndex].id
                val movieToGetRecommendationsTitle = movies[randomIndex].title
                //Showing only the year of release
                val movieToGetRecommendationsDate = movies[randomIndex].releaseDate.dropLast(6)

                var intent = showMovieDetailsForNotif(movies[randomIndex])
                intent.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }



                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

                var builder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_baseline_local_movies_24)
                    .setContentTitle("A movie you might like !")
                    .setContentText(movieToGetRecommendationsTitle + "(" + movieToGetRecommendationsDate + ")")
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(movieToGetRecommendationsTitle + " (" + movieToGetRecommendationsDate + ")"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this)) {
                    notify(0, builder.build())
                }
            }
            fun onError(){
                Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
            }
            getRecommendedMovies(
                id,
                recommendedMoviesPage,
                ::onRecommendedMoviesFetched2,
                ::onError
            )
        }


        val intent = intent
        val TYPE = intent.getIntExtra("type", 4)

        if(TYPE == 4)
        {
            val list = arrayListOf<Int>()

            reference_liked.get().addOnSuccessListener {
                if (it.exists() && it.childrenCount > 0) {
                    it.children.forEach { c ->
                        list.add(c.value.toString().toInt())
                    }
                    val randomIndex = Random.nextInt(list.size)
                    val movieToGetRecommendations = list[randomIndex]

                    getRecommendedMovies(movieToGetRecommendations)
                }
            }
        }



                // Friend request notifications
        reference.get().addOnSuccessListener {
            if (it.exists() && it.childrenCount > 0) {

                    var builder = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.profile)
                        .setContentTitle("Friend request")
                        .setContentText(it.childrenCount.toString()+" people want to be friends with you")
                        .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(it.childrenCount.toString()+" people want to be friends with you"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    with(NotificationManagerCompat.from(this)) {
                        notify(0, builder.build())
                    }
            }
        }


    }

    private fun onPopularMoviesFetched(movies: List<Movie>){
        Log.d("MoviesFragment", "Movies: $movies")
    }


    open fun replaceFragment(fragment: Fragment){
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}



class globalVars : Application() {
    companion object {
        var userID = ""
    }
}