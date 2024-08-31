package com.example.usthb9rayaadmin.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9rayaadmin.R
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.example.usthb9rayaadmin.ContributionDetailsActivity
import com.example.usthb9rayaadmin.DataClass.Contribution

class ContributionAdapter(private val context: Context, private val dataSet: Array<Contribution>) :
    RecyclerView.Adapter<ContributionAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullName: TextView = view.findViewById(R.id.FullName)
        val faculty: TextView = view.findViewById(R.id.Faculty)
        val module: TextView = view.findViewById(R.id.Module)
        val type: TextView = view.findViewById(R.id.Type)
        init {
            view.setOnClickListener {
                val i = Intent(context, ContributionDetailsActivity::class.java)
                i.putExtra("contribution", dataSet[adapterPosition])
                context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_contribution, viewGroup, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.fullName.text = dataSet[position].fullName
        viewHolder.faculty.text = dataSet[position].faculty
        viewHolder.module.text = dataSet[position].module
        viewHolder.type.text = dataSet[position].type
    }


    override fun getItemCount() = dataSet.size

}
