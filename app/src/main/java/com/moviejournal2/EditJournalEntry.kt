package com.moviejournal2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import java.lang.Exception
import android.provider.MediaStore
import android.text.Editable
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.io.File
import java.io.FileInputStream


class EditJournalEntry : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var binding: ActivityEditJournalEntryBinding
    private lateinit var storage: FirebaseStorage

    private var uploadImg = false
    private var uploadRec = false
    private var uploadType = 0
    private lateinit var recUri: Uri
    private lateinit var media: MediaPlayer

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun getMovie(id: Long): MovieUnit?{
        return db.daoMovie().findById(id)
    }

    private val db: AppDB by lazy{
        Room.databaseBuilder(
            applicationContext,
            AppDB::class.java,
            "movies.db"
        ).allowMainThreadQueries().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance("https://moviejournal2-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
        binding = ActivityEditJournalEntryBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()
        setContentView(binding.root)

        val b: Bundle? = getIntent().getExtras()
        val date = b?.getString("date")
        val savedate = b?.getString("savedate")
        val id = b?.getLong(MOVIE_ID)
//        val dataid = b?.getInt("dataid")
        val dataid = 0

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
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            uploadType = 1
            startActivityForResult(intent, EditJournalEntry.CHOOSE_PHOTO)
        }


        // Get data from database
        if (id != null && savedate != null) {
            // Set movie from id
            val movie = getMovie(id)
            fillDetails(movie!!)

            // Journal text
            reference.child(globalVars.Companion.userID).child("journal")
                .child(savedate).child(dataid.toString()).child("text").get().addOnSuccessListener {
                    if (it.value != null) {
                        binding.journalText.text = it.value.toString().toEditable()
                    }
            }

            // Journal image
            val path = "journal/" + globalVars.Companion.userID + "/" + savedate + "/" + dataid.toString()
            var storageRef = storage.reference.child(path+"/img.jpg")
            storageRef.downloadUrl.addOnSuccessListener { Uri->
                Glide.with(this)
                    .load(Uri.toString())
                    .into(binding.picture)
            }

            // Journal recording
            storageRef = storage.reference.child(path+"/rec.mp3")
            storageRef.downloadUrl.addOnSuccessListener { Uri->
//                Glide.with(this)
//                    .load(Uri.toString())
//                    .into(binding.recording)
                recUri = Uri
                setupMedia()
            }
        }


        // Save button
        binding.save.setOnClickListener {
            if (savedate != null) {
                reference.child(globalVars.Companion.userID).child("journal").child(savedate).get().addOnSuccessListener { it2 ->

                    // Check which id to use
                    var index = -1
                    it2.children.forEach { c->
                        if (c.key == dataid.toString()) {
                            index = dataid
                        }
                    }
                    if (index == -1) {
                        index = 0
                        while (index < it2.childrenCount) {
                            if (it2.child(index.toString()).value == null) {
                                break
                            }
                            index += 1
                        }
                    }

                    it2.child(index.toString()).child("text").ref.setValue(binding.journalText.text.toString())

                    var success = true

                    // Save picture and or recording
                    val path = "journal/" + globalVars.Companion.userID + "/" + savedate + "/" + index.toString()

                    if (uploadImg) {
                        val storageRef = storage.reference.child(path+"/img.jpg")
                        val bitmap = (binding.picture.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storageRef.putBytes(data).addOnFailureListener {
                            Toast.makeText(this, "Unable to save picture", Toast.LENGTH_SHORT).show()
                            success = false
                        }
                    }

                    if (uploadRec) {
                        val storageRef = storage.reference.child(path+"/rec.mp3")
                        storageRef.putFile(recUri).addOnFailureListener {
                            Toast.makeText(this, "Unable to save recording", Toast.LENGTH_SHORT).show()
                            success = false
                        }
                    }

                    if (success) {
                        Toast.makeText(this, "Journal entry saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun fillDetails(m: MovieUnit){
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w1280${m.backdropPath}")
            .transform(CenterCrop())
            .into(binding.backdrop)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${m.posterPath}")
            .transform(CenterCrop())
            .into(binding.moviePoster)

        binding.movieTitle.text = m.title
        binding.movieRating.rating = m.rating / 2
        binding.movieReleaseDate.text = m.releaseDate
        binding.movieOverview.text = m.overview
    }

    @SuppressLint("Range")
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
                recUri = data?.data!!
                setupMedia()
                uploadRec = true
            }
        }
    }

    private fun setupMedia() {
        media = MediaPlayer()
        media.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            media.setDataSource(this, recUri)
            media.prepare()

            binding.recording.visibility = GONE
            binding.listen.visibility = VISIBLE

            var fileName = ""
            var cursor: Cursor? = null
            cursor = contentResolver.query(
                recUri, arrayOf(
                    MediaStore.Images.ImageColumns.DISPLAY_NAME
                ), null, null, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            }
            if (cursor != null) {
                cursor.close()
            }

            binding.filename.setText(fileName)
            binding.play.setOnClickListener {
                media.start()
            }

        } catch(e: Exception) {
            android.widget.Toast.makeText(this, "Unable to load recording", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val CHOOSE_PHOTO = 2
    }
}