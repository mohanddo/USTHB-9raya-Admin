package com.example.usthb9rayaadmin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.Utils.FirebaseUtil.downloadFileToInternalStorage
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.databinding.ActivityContributionDetailsBinding
import java.io.File

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
    private lateinit var progressBar: ProgressBar
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

        if(contribution.fileDownloaded == "true") {
            downloadFileButt.visibility = View.GONE
            openFileButt.visibility = View.VISIBLE
        }

        contribution = intent.getParcelableExtra("contribution")!!
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
                    downloadFileToInternalStorage(this, contribution.contributionId, extension!!, progressBar, downloadFileButt)
            } else {
                Toast.makeText(this, "Error finding extension", Toast.LENGTH_SHORT).show()
            }

        }

        binding.DenyButt.setOnClickListener {
            val i = Intent(this, DenyContributionActivity::class.java)
            i.putExtra("contribution", contribution)
            startActivity(i)
        }

        openFileButt = binding.openFileButt
        openFileButt.setOnClickListener {
            val ext = Util.mimeTypeToExtension(contribution.mimeType)
            openFileFromInternalStorage(this, "${contribution.contributionId}.${ext}", contribution.mimeType)
        }
    }

    private fun openFileFromInternalStorage(context: Context, fileName: String, mimeType: String) {
        try {

            val file = File(context.getExternalFilesDir(null), fileName)

            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Error opening file, please try again.", Toast.LENGTH_LONG).show()
            Log.e("FileDownloader", "Error opening file: ${e}")
//            Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}