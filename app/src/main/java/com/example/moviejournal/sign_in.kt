package com.example.moviejournal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.content.Intent

class sign_in : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        var user = EditText(this)
        user = findViewById(R.id.username)
        var username = user.text.toString()
        var pass = EditText(this)
        pass = findViewById(R.id.password)
        var password = pass.text.toString()
        var login = Button(this)
        login = findViewById(R.id.login)
        login.setOnClickListener {
            if (username == "test") {
                val a = Intent(this, profile::class.java)
                startActivity(a)
            }
        }
    }
}