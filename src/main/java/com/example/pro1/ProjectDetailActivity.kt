package com.example.pro1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProjectDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_project_detail)

        val projectName =
            intent.getStringExtra("projectName")

        val title =
            findViewById<TextView>(R.id.project_title)

        title.text = projectName
    }
}