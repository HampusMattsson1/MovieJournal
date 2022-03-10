package com.moviejournal2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class sign_up : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val mlogin_button = findViewById<Button>(R.id.login_button)
        mlogin_button.setOnClickListener{
            startActivity(Intent(this@sign_up, sign_in::class.java))
        }
        val mbtn_register = findViewById<Button>(R.id.btn_register)
        val msignup_email = findViewById<TextInputEditText>(R.id.textEditEmail)
        val msignup_password = findViewById<TextInputEditText>(R.id.textEditPassword)
        if (mbtn_register != null && msignup_email != null && msignup_password != null){
            mbtn_register.setOnClickListener{
                when{
                    TextUtils.isEmpty(msignup_email.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(
                            this@sign_up,
                            "Please enter email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(msignup_password.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(
                            this@sign_up,
                            "Please enter password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val email: String = msignup_email.text.toString().trim {it <= ' '}
                val password: String = msignup_password.text.toString().trim {it <= ' '}

                //Create instance and registers a user with email and password
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OnCompleteListener { task ->

                        //If the registration is successful
                        if (task.isSuccessful){
                            //Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            Toast.makeText(
                                this@sign_up,
                                "You are registered successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Add entries to database & set global variable
                            globalVars.Companion.userID = firebaseUser.uid
                            database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
                            reference = database.getReference("users")
                            reference.get().addOnSuccessListener {
                                if (it.exists()) {
                                    reference.child(firebaseUser.uid).child("username").setValue("")
                                    reference.child(firebaseUser.uid).child("favmovie").setValue("")
                                    reference.child(firebaseUser.uid).child("genres").setValue("")
                                    reference.child(firebaseUser.uid).child("journal").setValue("")
                                    reference.child(firebaseUser.uid).child("friends").setValue("")
                                    reference.child(firebaseUser.uid).child("requests").setValue("")
                                }
                            }

                            /* The new user will be automatically signed-in so we sign him out
                            and send him to main screen with user id and email that user have used for
                            registration
                             */
                            val intent = Intent(this@sign_up, MainActivity::class.java)
                            /*gets rid of multiple layers (going back and forth between login and
                            register screens*/
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("user_id", firebaseUser.uid)
                            intent.putExtra("email_id", email)
                            startActivity(intent)
                            finish()
                        } else{
                            // If the registering is not successful then show error message.
                            Toast.makeText(
                                this@sign_up,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    })
            }
        }

    }
}