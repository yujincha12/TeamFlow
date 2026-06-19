package com.example.pro1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class ProjectAdapter(
    private val db: ProjectDB
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

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
        holder.projectMembers.text =
            "팀원 : ${item.members}"

        val dday = calculateDday(item.deadline)

        holder.projectDday.text =
            if (dday == 0L) {
                "프로젝트 D-Day"
            } else if (dday > 0L) {
                "프로젝트 D-$dday"
            } else {
                "프로젝트 D+${-dday}"
            }

        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                ProjectDetailActivity::class.java
            )

            intent.putExtra(
                "projectName",
                item.name
            )

            intent.putExtra(
                "members",
                item.members
            )

            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            db.deleteProject(item.id)
            projectList.removeAt(position)
            notifyItemRemoved(position)
            true
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

        val projectMembers: TextView =
            view.findViewById(R.id.project_members)

        val projectDday: TextView =
            view.findViewById(R.id.project_dday)
    }

    private fun calculateDday(dueDate: String): Long {
        return try {
            val format = java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
            )

            val today = format.parse(format.format(java.util.Date()))
            val target = format.parse(dueDate)

            val diff = target!!.time - today!!.time

            java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)
        } catch (e: Exception) {
            0L
        }
    }
}