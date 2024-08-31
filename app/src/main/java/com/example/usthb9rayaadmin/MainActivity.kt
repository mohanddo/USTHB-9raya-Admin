package com.example.usthb9rayaadmin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9rayaadmin.DataClass.Contribution
import com.example.usthb9rayaadmin.Utils.FirebaseUtil.contributionsRef
import com.example.usthb9rayaadmin.adapters.ContributionAdapter
import com.example.usthb9rayaadmin.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val progressBar = binding.progressBar
        progressBar.show()

        val recyclerView: RecyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val contributionsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                progressBar.hide()

                val contributionsList = mutableListOf<Contribution>()

                for (contributionSnapshot in dataSnapshot.children) {
                    val contribution = contributionSnapshot.getValue(Contribution::class.java)
                    contribution?.let { contributionsList.add(it) }
                }

                recyclerView.adapter = ContributionAdapter(this@MainActivity, contributionsList.toTypedArray())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Database Error", "loadPost:onCancelled", databaseError.toException())
            }
        }
            contributionsRef.addValueEventListener(contributionsListener)
    }
}