package com.example.usthb9rayaadmin

import android.content.Intent
import android.os.Bundle
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
import com.example.usthb9rayaadmin.Utils.FirebaseUtil.downloadFileToInternalStorage
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.Utils.Util.openFileFromInternalStorage
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
    private lateinit var contribution: Contribution
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var openFileButt: AppCompatButton
    private lateinit var downloadFileButt: AppCompatButton
    private var ext: String? = null

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
        progressBar = binding.progressBar
        downloadFileButt = binding.DownloadFileButt
        openFileButt = binding.openFileButt

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
        if(contribution.comment.isNotEmpty()) {
            comment.text = contribution.comment
            comment.visibility = View.VISIBLE
        }

        date.text = Util.calculateDateFromTimestamp(contribution.timestamp)

        downloadFileButt.setOnClickListener {

            ext = Util.mimeTypeToExtension(contribution.mimeType)
            val extension = ext
            if(ext != null) {
                    downloadFileToInternalStorage(this, contribution.contributionId, extension!!, progressBar, downloadFileButt, openFileButt, dataStore)
            } else {
                Toast.makeText(this, "Error finding extension", Toast.LENGTH_SHORT).show()
            }

        }

        binding.AcceptButt.setOnClickListener {
            val i = Intent(this, ConfirmContributionActivity::class.java)
            i.putExtra("contribution", contribution)
            startActivity(i)
        }

        openFileButt.setOnClickListener {
            val ext = Util.mimeTypeToExtension(contribution.mimeType)
            openFileFromInternalStorage(this, "${contribution.contributionId}.${ext}", contribution.mimeType)
        }
    }
}