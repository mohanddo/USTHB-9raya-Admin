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
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.DataClass.Youtube
import com.example.usthb9rayaadmin.Utils.FirebaseUtil
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.Utils.Util.alertDialog
import com.example.usthb9rayaadmin.Utils.Util.isEditTextEmpty
import com.example.usthb9rayaadmin.Utils.Util.sendEmail
import com.example.usthb9rayaadmin.databinding.ActivityConfirmContributionBinding
import java.io.File


class ConfirmContributionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmContributionBinding
    private lateinit var contribution: Contribution
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var progressBarYoutube: ContentLoadingProgressBar
    private lateinit var localFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmContributionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = binding.ProgressBar
        progressBar.show()
        progressBarYoutube = binding.ProgressBarYoutube

        contribution = intent.getParcelableExtra("contribution")!!

        val body = """
        Hello ${contribution.fullName},
        
        Your contribution has been confirmed! Here are the details:
        
        Faculty: ${contribution.faculty}
        
        Module: ${contribution.module}
        
        Type: ${contribution.type}
        
        Files: ${contribution.fileNames?.joinToString(", ")}
        
        Date: ${Util.calculateDateFromTimestamp(contribution.timestamp)}
        
        Thank you for your contribution!
        
        Best regards,
        USTHB 9raya Team
    """.trimIndent()

        binding.SendButt.setOnClickListener {
            sendEmail(this,
                contribution.email,
                "Thank you for your contribution",
                body
            )
        }

        val confirmFilesButt = binding.ConfirmFilesButt
        confirmFilesButt.setOnClickListener {

            alertDialog(this, "Accept contribution", "Are you sure you want to accept this contribution?",
                "Yes", "No", {
                    confirmFilesButt.isEnabled = false
                    progressBar.show()

                    contribution.fileNames?.let { fileNames ->
                        FirebaseUtil.deleteContributionFromFirebase(contribution.contributionId, {
                            Util.deleteFilesFromInternalStorage(this, contribution.contributionId, fileNames)
                            confirmFilesButt.visibility = View.GONE
                            val i = Intent(this, MainActivity::class.java)
                            i.flags = FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                        }, {
                            confirmFilesButt.isEnabled = true
                            progressBar.hide()
                            Toast.makeText(this, "There was an error please try again.", Toast.LENGTH_SHORT).show()
                        })
                    }
                }, { dialog ->
                    dialog.dismiss()
                })
        }

        val confirmYoutubeLinkButt = binding.ConfirmYoutubeLinkButt
        val inputTextName = binding.YoutubeName
        val inputTextLink = binding.YoutubeLink
        val inputTextFilter = binding.YoutubeFilterName
        confirmYoutubeLinkButt.setOnClickListener {
            confirmYoutubeLinkButt.isEnabled = false
            progressBarYoutube.show()

            if (!isEditTextEmpty(inputTextName) && !isEditTextEmpty(inputTextLink) && !isEditTextEmpty(inputTextFilter)) {

                val newElement = Youtube(inputTextName.text.toString(), inputTextLink.text.toString(), inputTextFilter.text.toString())

                Util.addElementToLocalFile(this, localFile, newElement) {
                    confirmYoutubeLinkButt.isEnabled = true
                    progressBarYoutube.hide()
                }

                Util.uploadFileToFirebase(localFile, {
                    Toast.makeText(this, "File uploaded to Firebase", Toast.LENGTH_SHORT).show()
                    confirmYoutubeLinkButt.visibility = View.GONE
                    progressBarYoutube.hide()
                }, { exception ->
                    Toast.makeText(this, "Failed to upload file: ${exception.message}", Toast.LENGTH_LONG).show()
                    confirmYoutubeLinkButt.isEnabled = true
                    progressBarYoutube.hide()
                })
            }
        }


        localFile = File(filesDir, "youtube_videos.json")

        Util.downloadFileFromFirebase(this, localFile) {
            progressBar.hide()
            binding.SendButt.visibility = View.VISIBLE

            contribution.fileNames?.let {
                confirmFilesButt.visibility = View.VISIBLE

            }

            contribution.youtubeLink?.let {
                inputTextLink.setText(it)
                inputTextName.visibility = View.VISIBLE
                inputTextLink.visibility = View.VISIBLE
                inputTextFilter.visibility = View.VISIBLE
                confirmYoutubeLinkButt.visibility = View.VISIBLE
            }
        }
    }


}