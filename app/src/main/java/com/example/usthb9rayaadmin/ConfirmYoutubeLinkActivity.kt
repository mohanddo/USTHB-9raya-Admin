package com.example.usthb9rayaadmin


import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.example.usthb9rayaadmin.DataClass.Youtube
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.Utils.Util.isEditTextEmpty
import com.example.usthb9rayaadmin.databinding.ActivityConfirmYoutubeLinkBinding
import java.io.File


class ConfirmYoutubeLinkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmYoutubeLinkBinding
    private lateinit var youtubeLink: String
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var localFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmYoutubeLinkBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = binding.ProgressBar
        progressBar.show()

        youtubeLink = intent.getStringExtra("youtubeLink")!!

        val confirmYoutubeLinkButt = binding.ConfirmYoutubeLinkButt
        val inputTextName = binding.YoutubeName
        val inputTextLink = binding.YoutubeLink
        inputTextLink.setText(youtubeLink)
        val inputTextFilter = binding.YoutubeFilterName
        confirmYoutubeLinkButt.setOnClickListener {
            confirmYoutubeLinkButt.isEnabled = false
            progressBar.show()

            if (!isEditTextEmpty(inputTextName) && !isEditTextEmpty(inputTextLink) && !isEditTextEmpty(inputTextFilter)) {

                val newElement = Youtube(inputTextName.text.toString(), inputTextLink.text.toString(), inputTextFilter.text.toString())

                Util.addElementToLocalFile(this, localFile, newElement) {
                    confirmYoutubeLinkButt.isEnabled = true
                    progressBar.hide()
                }

                Util.uploadFileToFirebase(localFile, {
                    Toast.makeText(this, "File uploaded to Firebase", Toast.LENGTH_SHORT).show()
                    finish()
                }, { exception ->
                    Toast.makeText(this, "Failed to upload file: ${exception.message}", Toast.LENGTH_LONG).show()
                    confirmYoutubeLinkButt.isEnabled = true
                    progressBar.hide()
                })
            }
        }


        localFile = File(filesDir, "youtube_videos.json")

        Util.downloadFileFromFirebase(this, localFile) {
            progressBar.hide()

            binding.container.visibility = View.VISIBLE
        }

    }
}