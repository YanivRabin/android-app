package com.example.viewandroidapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewandroidapp.databinding.ActivityProfileBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = PostAdapter(generatePosts())

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, profileButton)
    }

    // Generate dummy list of posts (replace with dataBase)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePosts(): ArrayList<Post> {
        val posts = arrayListOf<Post>()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatter)
        for (i in 1..5) {
            posts.add(Post(
                R.drawable.profile_picture,
                "Yaniv is at location",
                currentDate,
                "This is my flight to location",
                R.drawable.background
            ))
        }
        return posts
    }

    fun onIconSettingsClick(view: View) {
        // open setting
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
}