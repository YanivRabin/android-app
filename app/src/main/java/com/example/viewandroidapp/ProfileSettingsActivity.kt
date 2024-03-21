package com.example.viewandroidapp

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.example.viewandroidapp.Model.FireBaseModel
import com.example.viewandroidapp.databinding.ActivityProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var fireBaseModel: FireBaseModel
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile_settings)

        fireBaseModel = FireBaseModel()
        auth = FirebaseAuth.getInstance()
        fetchAndDisplayUserData()
    }
    private fun fetchAndDisplayUserData() {
        // Get the currently logged-in user
        val currentUser = auth.currentUser

        // Check if user is signed in (not null)
        currentUser?.let { user ->
            // Get the email of the logged-in user
            val userEmail = user.email

            // Fetch user data from Firestore using the logged-in user's email
            if (userEmail != null) {
                fireBaseModel.getUser(userEmail) { userData ->
                    userData?.let { user ->
                        // User data retrieved successfully
                        // Display the user's name and profile photo in the UI
                        binding.userName.setText(user.name)
                        // Load profile photo into ImageView using Picasso or Glide
                        if (!user.profileImage.isNullOrEmpty()) {
                            Picasso.get()
                                .load(user.profileImage)
                                .into(binding.profilePicture)                        }
                    } ?: run {
                        // User not found or error occurred
                        // Handle the situation accordingly
                        // For example, display a default name or an error message
                        binding.userName.setText( "User Not Found")
                    }
                }
            } else { }
        } ?: run {
            // Handle case where no user is signed in
            binding.userName.setText("User Not Logged In")
        }
    }
    fun onIconCloseClick(view: View) {
        finish()
    }

    fun onIconLogoutClick(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            // logout action

            // dismiss window and go back to login page
            dialogInterface.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.logout))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
        }
        alertDialog.show()
    }

    fun changeProfilePictureClick(view: View) {
        // Create an intent to open the image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            fireBaseModel.uploadPhoto(selectedImageUri.toString(),"profile_image",onSuccess = { photoUrl ->
                // Photo uploaded successfully, now save it to the user's db
                val currentUserEmail = auth.currentUser?.email.toString()
                fireBaseModel.updateProfileImage(currentUserEmail, photoUrl,
                    onSuccess = {
                        // Profile image updated successfully
                        Log.e("ProfileSettingsActivity", "Profile image updated successfully")
                        // Show a success message or perform any other action if needed
                        Toast.makeText(
                            baseContext, "Profile image updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                        startActivity(intent)
                        finish()
                    },
                    onFailure = { exception ->
                        // Handle failure
                        Log.e("ProfileSettingsActivity", "Error updating profile image: $exception")
                        Toast.makeText(
                            baseContext, "Error updating profile image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },onFailure = { exception ->
                // Handle failure
                Log.e("ProfileSettingsActivity", "Error uploading profile image: $exception")
                Toast.makeText(
                    baseContext, "Error uploading profile image.",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }
    }
    fun onIconCheckClick(view: View) {
        // Change name in the database
        val newName = findViewById<EditText>(R.id.userName).text.toString()
        if (newName.isNotEmpty()) { // Check if the newName is not empty
            val userEmail = auth.currentUser?.email ?: ""

            fireBaseModel.updateUserName(userEmail, newName,
                onSuccess = {
                    // Name updated successfully
                    Log.e("ProfileSettingsActivity", "Name updated successfully")
                    // Show a success message or perform any other action if needed
                    Toast.makeText(
                        baseContext, "Name updated successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    // Handle failure
                    Log.e("ProfileSettingsActivity", "Error updating name: $exception")
                    Toast.makeText(
                        baseContext, "Error updating name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } else {
            // Show an error message or perform any other action if the new name is empty
            Toast.makeText(
                baseContext, "Please enter a non-empty name.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }







}