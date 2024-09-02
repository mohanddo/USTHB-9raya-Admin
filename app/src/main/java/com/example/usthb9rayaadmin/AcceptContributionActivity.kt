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
import com.example.usthb9rayaadmin.databinding.ActivityAcceptContributionBinding


class AcceptContributionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAcceptContributionBinding
    private lateinit var contribution: Contribution
    private var removedContributionFromDB = false
    private var removedFileFromStorage = false
    private lateinit var progressBar: ContentLoadingProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptContributionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = binding.ProgressBar

        contribution = intent.getParcelableExtra("contribution")!!

        binding.SendButt.setOnClickListener {
            sendEmail(this,
                contribution.email
                , "Sorry",
                binding.message.text.toString())

        }
        val confirmButt = binding.ConfirmButt
        confirmButt.setOnClickListener {

            alertDialog(this, "Accept contribution", "Are you sure you want to accept this contribution?",
                "Yes", "No", {
                    confirmButt.isEnabled = false
                    progressBar.show()

                    val ext = Util.mimeTypeToExtension(contribution.mimeType)

                    if(!removedContributionFromDB) {
                        FirebaseUtil.contributionsRef.child(contribution.contributionId).removeValue().addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                removedContributionFromDB = true
                                if (removedFileFromStorage) {
                                    Util.deleteFileFromInternalStorage(this, "${contribution.contributionId}.${ext}")
                                    val i = Intent(this, MainActivity::class.java)
                                    i.flags = FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(i)

                                }
                            } else {
                                confirmButt.isEnabled = true
                                progressBar.hide()
                                Toast.makeText(this, "There was an error please try again.", Toast.LENGTH_SHORT).show()
                                Log.e("AcceptContributionActivity", "Error removing contribution from database: ${task.exception}")
                            }
                        }
                    }

                    if(!removedFileFromStorage) {
                        FirebaseUtil.storageRef.child("uploads/${contribution.contributionId}").delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                removedFileFromStorage = true
                                if(removedContributionFromDB) {
                                    Util.deleteFileFromInternalStorage(this, "${contribution.contributionId}.${ext}")
                                    val i = Intent(this, MainActivity::class.java)
                                    i.flags = FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(i)

                                }
                            } else {
                                confirmButt.isEnabled = true
                                progressBar.hide()
                                Toast.makeText(this, "There was an error please try again.", Toast.LENGTH_SHORT).show()
                                Log.e("AcceptContributionActivity", "Error removing file from storage: ${task.exception}")
                            }
                        }
                    }

                }, { dialog ->
                    dialog.dismiss()
                })
        }

    }


}