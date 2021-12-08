package com.moviejournal2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moviejournal2.databinding.ActivityEditJournalEntryBinding
import com.moviejournal2.databinding.FragmentJournalBinding

class EditJournalEntry : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityEditJournalEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityEditJournalEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val b: Bundle? = getIntent().getExtras()
        val date = b?.getString("date")

        binding.date.setText(date)



    }
}