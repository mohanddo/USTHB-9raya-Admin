package com.example.usthb9rayaadmin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.Manifest
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.Utils.FirebaseUtil
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

        contribution = intent.getParcelableExtra("contribution")!!
        fullName.text = contribution.fullName
        email.text = contribution.email
        faculty.text = contribution.faculty
        module.text = contribution.module
        type.text = contribution.type
        contribution.comment?.let {
            comment.text = it
            comment.visibility = View.VISIBLE
        }
        date.text = Util.calculateDateFromTimestamp(contribution.timestamp)

        binding.DownloadFileButt.setOnClickListener {

            ext = Util.mimeTypeToExtension(contribution.mimeType)
            val extension = ext
            if(ext != null) {
                    downloadFileToInternalStorage(this, contribution.contributionId, extension!!)
            } else {
                Toast.makeText(this, "Error finding extension", Toast.LENGTH_SHORT).show()
            }

        }

        binding.openFileButt.setOnClickListener {
        }
    }
}