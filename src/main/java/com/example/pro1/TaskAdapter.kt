package com.example.pro1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.widget.CheckBox
import android.graphics.Color
import android.graphics.Paint

class TaskAdapter(
    private val db: TaskDB,
    private val onStatusChanged: () -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var taskList = ArrayList<TaskModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.task_row,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = taskList[position]

        holder.taskCheck.setOnCheckedChangeListener(null)

        holder.taskCheck.text = item.task
        holder.taskCheck.isChecked = item.status != 0

        if (item.status != 0) {

            holder.taskCheck.paintFlags =
                holder.taskCheck.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.taskCheck.setTextColor(Color.GRAY)

            holder.memberText.setTextColor(Color.GRAY)

            holder.ddayText.setTextColor(Color.GRAY)

        } else {

            holder.taskCheck.paintFlags =
                holder.taskCheck.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            holder.taskCheck.setTextColor(Color.BLACK)

            holder.memberText.setTextColor(Color.BLACK)

            holder.ddayText.setTextColor(Color.BLACK)
        }

        holder.taskCheck.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
            onStatusChanged()
        }

        holder.memberText.text =
            "담당자 : ${item.member}"

        val dday = calculateDday(item.dueDate)

        val ddayText =
            if (dday == 0L) {
                "D-Day"
            } else if (dday > 0L) {
                "D-$dday"
            } else {
                "D+${-dday}"
            }

        holder.ddayText.text =
            "$ddayText / 마감일: ${item.dueDate}"

        holder.itemView.setOnLongClickListener {
            db.deleteTask(item.id)
            taskList.removeAt(position)
            notifyItemRemoved(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setTask(list: ArrayList<TaskModel>) {

        taskList = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val taskCheck: CheckBox =
            view.findViewById(R.id.task_check)

        val memberText: TextView =
            view.findViewById(R.id.member_text)

        val ddayText: TextView =
            view.findViewById(R.id.dday_text)
    }

    private fun calculateDday(dueDate: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val today = format.parse(format.format(Date()))
            val target = format.parse(dueDate)

            val diff = target!!.time - today!!.time

            TimeUnit.MILLISECONDS.toDays(diff)
        } catch (e: Exception) {
            0L
        }
    }
}