package com.example.moviejournal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import android.widget.Toast

class search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var menuButton1 = ImageButton(this)
        menuButton1 = findViewById(R.id.imageButton1)
        menuButton1.setOnClickListener {
            val a = Intent(this, journal::class.java)
            startActivity(a)
        }
        var menuButton2 = ImageButton(this)
        menuButton2 = findViewById(R.id.imageButton2)
        menuButton2.setOnClickListener {
            val a = Intent(this, people::class.java)
            startActivity(a)
        }
        var menuButton3 = ImageButton(this)
        menuButton3 = findViewById(R.id.imageButton3)
        menuButton3.setOnClickListener {
            val a = Intent(this, films::class.java)
            startActivity(a)
        }
        var menuButton4 = ImageButton(this)
        menuButton4 = findViewById(R.id.imageButton4)
        menuButton4.setOnClickListener {
            val a = Intent(this, search::class.java)
            startActivity(a)
        }
        var menuButton5 = ImageButton(this)
        menuButton5 = findViewById(R.id.imageButton5)
        menuButton5.setOnClickListener {
            val a = Intent(this, profile::class.java)
            startActivity(a)
        }

        // temp code
        // var temp = Button(this)
        var temp = findViewById<Button>(R.id.button)
        temp.setOnClickListener {
            val a = Intent(this, login::class.java)
            startActivity(a)
        }
        var api = findViewById<Button>(R.id.button3)
        api.setOnClickListener {
//            val a = Intent(this, login::class.java)
//            startActivity(a)
            var msg = "hej"
            Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
//            run("https://imdb-api.com/en/API/SearchMovie/k_12345678/inception 2010")
        }
    }
}