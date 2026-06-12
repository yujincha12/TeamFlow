package com.example.pro1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class ProjectAdapter :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private var projectList: ArrayList<ProjectModel> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.project_item,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = projectList[position]

        holder.projectName.text = item.name

        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                ProjectDetailActivity::class.java
            )

            intent.putExtra(
                "projectName",
                item.name
            )

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    fun setProject(projectList: ArrayList<ProjectModel>) {

        this.projectList = projectList

        notifyDataSetChanged()
    }

    class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val projectName: TextView =
            view.findViewById(R.id.project_name)
    }
}