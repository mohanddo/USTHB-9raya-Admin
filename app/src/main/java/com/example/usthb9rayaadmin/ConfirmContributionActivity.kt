package com.example.usthb9rayaadmin


import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.Utils.FirebaseUtil
import com.example.usthb9rayaadmin.Utils.Util
import com.example.usthb9rayaadmin.Utils.Util.alertDialog
import com.example.usthb9rayaadmin.Utils.Util.sendEmail
import com.example.usthb9rayaadmin.databinding.ActivityConfirmContributionBinding


class ConfirmContributionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmContributionBinding
    private lateinit var contribution: Contribution
    private lateinit var progressBar: ContentLoadingProgressBar
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

        contribution = intent.getParcelableExtra("contribution")!!

        val body = """
        Hello ${contribution.fullName},
        
        Your contribution has been confirmed! Here are the details:
        
        Faculty: ${contribution.faculty}
        Module: ${contribution.module}
        Type: ${contribution.type}
        Files: ${contribution.fileNames.joinToString(", ")}
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
        val confirmButt = binding.ConfirmButt
        confirmButt.setOnClickListener {

            alertDialog(this, "Accept contribution", "Are you sure you want to accept this contribution?",
                "Yes", "No", {
                    confirmButt.isEnabled = false
                    progressBar.show()

                    FirebaseUtil.deleteContributionFromFirebase(contribution.contributionId, {
                        Util.deleteFilesFromInternalStorage(this, contribution.contributionId, contribution.fileNames)
                        val i = Intent(this, MainActivity::class.java)
                        i.flags = FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }, {
                        confirmButt.isEnabled = true
                        progressBar.hide()
                        Toast.makeText(this, "There was an error please try again.", Toast.LENGTH_SHORT).show()
                    })


                }, { dialog ->
                    dialog.dismiss()
                })
        }

    }


}