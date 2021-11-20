package com.moviejournal2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class sign_in : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val mregister_button = findViewById<Button>(R.id.register_button)
        val msignin_email = findViewById<TextInputEditText>(R.id.login_textEditEmail)
        val msignin_password = findViewById<TextInputEditText>(R.id.login_textEditPassword)
        mregister_button.setOnClickListener{
            startActivity(Intent(this@sign_in, sign_up::class.java))
        }

        val mbtn_login = findViewById<Button>(R.id.btn_login)
        mbtn_login.setOnClickListener{
            when{
                TextUtils.isEmpty(msignin_email.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(
                        this@sign_in,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(msignin_password.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(
                        this@sign_in,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = msignin_email.text.toString().trim {it <= ' '}
                    val password: String = msignin_password.text.toString().trim {it <= ' '}

                    //Login with FirebaseAuth
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener{ task ->

                            if (task.isSuccessful){
                                Toast.makeText(
                                    this@sign_in,
                                    "You are logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@sign_in, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            }else{
                                //If login is not successful
                                Toast.makeText(
                                    this@sign_in,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}