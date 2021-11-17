package com.example.moviejournal;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent

class login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        var user = EditText(this)
        user = findViewById(R.id.username)
        var pass = EditText(this)
        pass = findViewById(R.id.password)
        var login = Button(this)
        login = findViewById(R.id.login)
        login.setOnClickListener {
            var username = user.text.toString()
            var password = pass.text.toString()
            if (username == "admin" && password == "admin") {
                val a = Intent(this, profile::class.java)
                startActivity(a)
            }
        }
    }
}