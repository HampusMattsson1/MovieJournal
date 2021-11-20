package com.moviejournal2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.moviejournal2.fragments.*

class MainActivity : AppCompatActivity() {
    private val journalFragment = JournalFragment()
    private val friendsFragment = FriendsFragment()
    private val moviesFragment = MoviesFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(moviesFragment)

        val bottom_nav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
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
        val mbtn_logout = findViewById<Button>(R.id.btn_logout)
        mbtn_logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, sign_up::class.java))
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment !=null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}