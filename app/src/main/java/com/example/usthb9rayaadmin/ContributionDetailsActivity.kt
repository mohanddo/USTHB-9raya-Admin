package com.example.usthb9rayaadmin

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.Utils.DataStoreProvider
import com.example.usthb9rayaadmin.Utils.FirebaseUtil
import com.example.usthb9rayaadmin.Utils.FirebaseUtil.downloadFileToInternalStorage
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.Utils.Util.alertDialog
import com.example.usthb9rayaadmin.Utils.Util.extensionToMimeType
import com.example.usthb9rayaadmin.Utils.Util.getFileExtension
import com.example.usthb9rayaadmin.Utils.Util.openFileFromInternalStorage
import com.example.usthb9rayaadmin.Utils.Util.openYouTubeLink
import com.example.usthb9rayaadmin.Utils.Util.sendEmail
import com.example.usthb9rayaadmin.Utils.Util.singleChoiceDialog
import com.example.usthb9rayaadmin.databinding.ActivityContributionDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContributionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContributionDetailsBinding
    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var faculty: TextView
    private lateinit var module: TextView
    private lateinit var type: TextView
    private lateinit var comment: TextView
    private lateinit var date: TextView
    private lateinit var youtubeLink: TextView
    private lateinit var fileName: TextView
    private lateinit var contribution: Contribution
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var openFileButt: AppCompatButton
    private lateinit var downloadFileButt: AppCompatButton
    private lateinit var openLinkButt: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContributionDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullName = binding.FullName
        email = binding.email
        faculty = binding.faculty
        module = binding.module
        type = binding.type
        comment = binding.comment
        date = binding.date
        youtubeLink = binding.youtubeLink
        fileName = binding.fileName
        progressBar = binding.progressBar
        downloadFileButt = binding.DownloadFileButt
        openFileButt = binding.openFileButt
        openLinkButt = binding.openLinkButt

        contribution = intent.getParcelableExtra("contribution")!!

        val dataStore = DataStoreProvider.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            // Perform background task
            val isFileDownloaded = Util.read(contribution.contributionId, dataStore) ?: "false"

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                if (isFileDownloaded == "true") {
                    downloadFileButt.visibility = View.GONE
                    openFileButt.visibility = View.VISIBLE
                }
            }
        }


        fullName.text = contribution.fullName
        email.text = contribution.email
        faculty.text = contribution.faculty
        module.text = contribution.module
        type.text = contribution.type
        contribution.comment?.let {
            comment.text = contribution.comment
            comment.visibility = View.VISIBLE
        }


        Log.e("Timestamp", contribution.timestamp.toString())
        date.text = Util.calculateDateFromTimestamp(contribution.timestamp)

        contribution.fileNames?.let { fileNames ->

            fileName.setOnClickListener {
                singleChoiceDialog(this, fileNames.toTypedArray(), "Choose a file", fileName)
            }

            downloadFileButt.visibility = View.VISIBLE
            downloadFileButt.setOnClickListener {
                downloadFileToInternalStorage(this, fileNames, contribution.contributionId, contribution.filesSize, progressBar, downloadFileButt, openFileButt, dataStore)
            }
        }

        contribution.youtubeLink?.let { youtubeLinkString ->

            binding.confirmYoutubeLinkButt.visibility = View.VISIBLE
            youtubeLink.visibility = View.VISIBLE

            openLinkButt.visibility = View.VISIBLE
            openLinkButt.setOnClickListener {
                openYouTubeLink(this, youtubeLinkString)
            }
        }

        binding.confirmYoutubeLinkButt.setOnClickListener {
            val i = Intent(this, ConfirmYoutubeLinkActivity::class.java)
            i.putExtra("youtubeLink", contribution.youtubeLink!!)
            startActivity(i)
        }

        openFileButt.setOnClickListener {

            if(fileName.text.isEmpty()) {
                Toast.makeText(this, "Please choose a file first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val fileName = fileName.text.toString()
                val extension = getFileExtension(fileName)
                val mimeType = extensionToMimeType(extension)
                openFileFromInternalStorage(this, fileName, mimeType!!)
            }
        }

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

        binding.deleteButt.setOnClickListener {
            alertDialog(this, "Accept contribution", "Are you sure you want to accept this contribution?",
                "Yes", "No", {
                    progressBar.show()


                        FirebaseUtil.deleteContributionFromFirebase(contribution.contributionId, {
                            contribution.fileNames?.let { fileNames ->
                                Util.deleteFilesFromInternalStorage(this, contribution.contributionId, fileNames)
                            }
                            val i = Intent(this, MainActivity::class.java)
                            i.flags = FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                        }, {
                            progressBar.hide()
                            Toast.makeText(this, "There was an error please try again.", Toast.LENGTH_SHORT).show()
                        })

                }, { dialog ->
                    dialog.dismiss()
                })
        }
    }
}