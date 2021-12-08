package com.moviejournal2

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toDrawable
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moviejournal2.databinding.ActivityEditJournalEntryBinding
import com.moviejournal2.databinding.FragmentJournalBinding
import java.io.ByteArrayOutputStream

class EditJournalEntry : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityEditJournalEntryBinding
    private lateinit var storage: FirebaseStorage

    private var uploadImg = false
    private var uploadRec = false
    private var uploadType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityEditJournalEntryBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()
        setContentView(binding.root)

        val b: Bundle? = getIntent().getExtras()
        val date = b?.getString("date")

        binding.date.setText(date)

        // Upload picture
        binding.picture.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            uploadType = 0
            startActivityForResult(intent, EditJournalEntry.CHOOSE_PHOTO)
        }

        // Upload recording
        binding.recording.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "audio/*"
            uploadType = 1
            startActivityForResult(intent, EditJournalEntry.CHOOSE_PHOTO)
        }


        // Save button
        binding.save.setOnClickListener {
            if (date != null) {
                reference.child(globalVars.Companion.userID).child("journal").child(date).get().addOnSuccessListener { it2 ->
                    it2.child("text").ref.setValue(binding.journalText.text.toString())

                    var success = false

                    // Save picture and or recording
                    if (uploadImg) {
                        val path = "journal/" + globalVars.Companion.userID + "/" + date + "/img.jpg"
                        val storageRef = storage.reference.child(path)
                        val bitmap = (binding.picture.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storageRef.putBytes(data).addOnFailureListener {
                            Toast.makeText(this, "Unable to save picture", Toast.LENGTH_SHORT).show()
                        }.addOnSuccessListener {
                            success = true
                        }
                    }

                    if (uploadRec) {

                    }
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (uploadType == 0) {
            if (resultCode == Activity.RESULT_OK && requestCode == EditJournalEntry.CHOOSE_PHOTO){
                var img: ImageView = binding.picture
                img.setImageURI(data?.data)
                uploadImg = true
            }
        }
        if (uploadType == 1) {
            if (resultCode == Activity.RESULT_OK && requestCode == EditJournalEntry.CHOOSE_PHOTO){
                val imageUri = Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.done)
                        + '/' + getResources().getResourceTypeName(R.drawable.done) + '/' + getResources().getResourceEntryName(R.drawable.done) )
                binding.recording.setImageURI(imageUri)
                uploadRec = true
            }
        }
    }

    companion object {
        private val CHOOSE_PHOTO = 2
    }
}