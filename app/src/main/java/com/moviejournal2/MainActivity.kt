package com.moviejournal2

import MoviesAdapter
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.fragments.*

class MainActivity : AppCompatActivity() {
    private val journalFragment = JournalFragment()
    private val friendsFragment = FriendsFragment()
    private val moviesFragment = MoviesFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager

    private val channelId = "12345"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(moviesFragment)



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

        // Friend request notifications
//        reference.get().addOnSuccessListener {
//            if (it.exists() && it.childrenCount > 0) {
//                it.children.forEach { c ->
//                    var builder = NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.profile)
//                        .setContentTitle("Friend request")
//                        .setContentText("Much longer text that cannot fit one line...")
//                        .setStyle(NotificationCompat.BigTextStyle()
//                            .bigText("Much longer text that cannot fit one line..."))
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//                    notificationChannel = NotificationChannel(channelId, "test", NotificationManager .IMPORTANCE_HIGH)
//                    notificationChannel.lightColor = Color.BLUE
//                    notificationChannel.enableVibration(true)
//
//                    notificationManager.createNotificationChannel(notificationChannel)
//
//                    notificationManager.notify(12345, builder.build())
//                }
//            }
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

    fun replaceFragment(fragment: Fragment){
        if(fragment !=null) {
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