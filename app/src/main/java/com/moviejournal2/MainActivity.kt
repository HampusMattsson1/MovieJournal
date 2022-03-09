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
            //Your code once the alarm is set off goes here
            //You can use an intent filter to filter the specified intent
                val intent1 = Intent(context, MainActivity::class.java)
                intent1.putExtra("type", 4)
                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent1)
                }
            }






    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val b: Bundle? = getIntent().getExtras()
        val fragment = b?.getInt("fragment")
        if (fragment == 0) {
            replaceFragment(moviesFragment)
        } else {
            replaceFragment(journalFragment)
        }

        val bottom_nav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
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
            val mAlarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            //Create the time of day you would like it to go off. Use a calendar
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 20)
            calendar.set(Calendar.MINUTE, 40)

            //Create an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this,
                DATA_FETCHER_RC,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            //initialize the alarm by using inexactrepeating. This allows the system to scheduler your alarm at the most efficient time around your
            //set time, it is usually a few seconds off your requested time.
            // you can also use setExact however this is not recommended. Use this only if it must be done then.

            //Also set the interval using the AlarmManager constants
            mAlarmManager.setInexactRepeating(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        createAlarm()



        /*
        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        val mtv_user_id = findViewById<TextView>(R.id.tv_user_id)
        val mtv_email_id = findViewById<TextView>(R.id.tv_email_id)
        mtv_user_id.text = "User ID :: $userId"
        mtv_email_id.text = "Email ID :: $emailId"
        */

        //Logout button
        /*
        val mbtn_logout = findViewById<Button>(R.id.btn_logout)
        mbtn_logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, sign_up::class.java))
            finish()
        }
        */

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users/" + globalVars.Companion.userID + "/requests")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        reference_liked = database.getReference("users/" + globalVars.Companion.userID + "/likedlist")

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
                /*var intent = Intent(this, MovieInfoActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }*/

                /*var movieNotifAdapter = MoviesAdapter(mutableListOf()) { movie -> intent =
                    showMovieDetails(movie)}*/


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
                Log.i("didi", movieToGetRecommendationsTitle.toString())
            }
            fun onError(){
                Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
            }
            MoviesRepository.getRecommendedMovies(
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

                    Log.d("hehe", movieToGetRecommendations.toString())
                    getRecommendedMovies(movieToGetRecommendations)
                }
            }
        }



                // Friend request notifications
        reference.get().addOnSuccessListener {
            if (it.exists() && it.childrenCount > 0) {
//                it.children.forEach { c ->
//                    database.getReference("users/"+c.value.toString()+"/username").get()
//                        .addOnSuccessListener { it2->
//                        var builder = NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.profile)
//                            .setContentTitle("Friend request")
//                            .setContentText(it2.value.toString()+" wants to be your friend!")
//                            .setStyle(NotificationCompat.BigTextStyle()
//                                .bigText("Much longer text that cannot fit one line..."))
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//                        with(NotificationManagerCompat.from(this)) {
//                            notify(c.key!!.toInt(), builder.build())
//                        }
//                    }
//                }

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

//        var builder = NotificationCompat.Builder(this)
//            .setSmallIcon(R.drawable.profile)
//            .setContentTitle("Friend request")
//            .setContentText("Much longer text that cannot fit one line...")
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText("Much longer text that cannot fit one line..."))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        with(NotificationManagerCompat.from(this)) {
//            notify(12345, builder.build())
//        }

    }

    private fun onPopularMoviesFetched(movies: List<Movie>){
        Log.d("MoviesFragment", "Movies: $movies")
    }

    /*
    private fun onError(){
        Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }
    */

    open fun replaceFragment(fragment: Fragment){
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}

//class changeFragment : MainActivity() {
//    fun replaceFragment(fragment: Fragment){
//        if(fragment !=null) {
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, fragment)
//            transaction.commit()
//        }
//    }
//}


class globalVars : Application() {
    companion object {
        var userID = ""
    }
}