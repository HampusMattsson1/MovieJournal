package com.moviejournal2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.databinding.ActivityEditJournalEntryBinding
import com.moviejournal2.databinding.ActivityViewFriendBinding
import java.text.SimpleDateFormat

class EditJournalEntry : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityEditJournalEntryBinding
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        storage = FirebaseStorage.getInstance()
        binding = ActivityEditJournalEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val b: Bundle? = getIntent().getExtras()
        val date = SimpleDateFormat("dd/MM/yyyy").format(b?.getLong("date"))

        if (date != null) {
            binding.date.setText(date)
            Toast.makeText(this, date, Toast.LENGTH_SHORT).show()
        }




    }
}