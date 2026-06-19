package com.example.pro1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.DatePickerDialog
import java.util.Calendar
import android.content.Context
import android.widget.Toast

class ProjectDetailActivity : AppCompatActivity() {

    lateinit var projectName: String

    lateinit var db: TaskDB

    lateinit var adapter: TaskAdapter

    lateinit var recyclerView: RecyclerView

    lateinit var taskInput: EditText

    lateinit var addBtn: Button

    lateinit var memberInput: EditText

    lateinit var dateInput: EditText

    lateinit var urgentText: TextView

    lateinit var progressText: TextView

    lateinit var myNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_project_detail
        )

        projectName =
            intent.getStringExtra(
                "projectName"
            ) ?: ""

        val members =
            intent.getStringExtra(
                "members"
            ) ?: ""

        findViewById<TextView>(
            R.id.project_title
        ).text = projectName

        findViewById<TextView>(
            R.id.project_members
        ).text = "참여 구성원 : $members"

        db = TaskDB(this)

        recyclerView =
            findViewById(R.id.task_recycler)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter = TaskAdapter(db) {
            loadTasks()
        }

        recyclerView.adapter = adapter

        taskInput =
            findViewById(R.id.task_input)

        addBtn =
            findViewById(R.id.add_task_btn)

        memberInput =
            findViewById(R.id.member_input)

        myNameInput =
            findViewById(R.id.my_name_input)

        val prefs =
            getSharedPreferences("user", Context.MODE_PRIVATE)

        myNameInput.setText(
            prefs.getString("myName", "")
        )

        myNameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                prefs.edit()
                    .putString("myName", myNameInput.text.toString())
                    .apply()

                loadTasks()
            }
        }

        dateInput =
            findViewById(R.id.date_input)

        dateInput.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val dateText =
                        String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                        )

                    dateInput.setText(dateText)
                },
                year,
                month,
                day
            )

            dialog.show()
        }

        urgentText =
            findViewById(R.id.urgent_text)

        progressText =
            findViewById(R.id.progress_text)

        loadTasks()

        addBtn.setOnClickListener {

            val text =
                taskInput.text.toString()

            val member =
                memberInput.text.toString()

            val dueDate =
                dateInput.text.toString()

            if(
                text.isNotEmpty() &&
                member.isNotEmpty() &&
                dueDate.isNotEmpty()
            ) {

                db.addTask(
                    TaskModel(
                        0,
                        projectName,
                        text,
                        member,
                        dueDate,
                        0
                    )
                )

                taskInput.setText("")
                memberInput.setText("")
                dateInput.setText("")

                loadTasks()
            }else {

                Toast.makeText(
                    this,
                    "할 일, 담당자, 마감일을 모두 입력하세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadTasks() {

        val list =
            db.getTasks(projectName)

        adapter.setTask(list)

        var doneCount = 0

        for (task in list) {
            if (task.status == 1) {
                doneCount++
            }
        }

        val progress =
            if (list.isNotEmpty()) {
                doneCount * 100 / list.size
            } else {
                0
            }

        progressText.text =
            "진행률 ${progress}% ($doneCount/${list.size})"

        var urgent = "⚠ 마감 임박 일정\n"

        for (task in list) {
            val dday = calculateDday(task.dueDate)

            val myName = myNameInput.text.toString()

            if (dday in 0..5 && (myName.isEmpty() || task.member.contains(myName))) {
                urgent += "${task.task} / 담당자: ${task.member} / D-$dday\n"
            }
        }

        urgentText.text =
            if (urgent == "⚠ 마감 임박 일정\n") {
                ""
            } else {
                urgent
            }


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
            999L
        }
    }
}