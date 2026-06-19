package com.example.pro1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDB(context: Context)
    : SQLiteOpenHelper(
    context,
    "task.db",
    null,
    4
) {

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(
            "CREATE TABLE task (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "projectName TEXT," +
                    "task TEXT," +
                    "member TEXT," +
                    "dueDate TEXT," +
                    "status INTEGER)"
        )
    }
    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

        db?.execSQL("DROP TABLE IF EXISTS task")

        onCreate(db)
    }
    fun openDatabase() {
        db = writableDatabase
    }

    fun addTask(taskModel: TaskModel) {

        openDatabase()

        val cv = ContentValues()

        cv.put(
            "projectName",
            taskModel.projectName
        )

        cv.put(
            "task",
            taskModel.task
        )

        cv.put(
            "member",
            taskModel.member
        )

        cv.put(
            "dueDate",
            taskModel.dueDate
        )

        cv.put("status", 0)

        db!!.insert(
            "task",
            null,
            cv
        )
    }

    fun getTasks(
        projectName: String
    ): ArrayList<TaskModel> {

        val list =
            ArrayList<TaskModel>()

        db = readableDatabase

        val cursor: Cursor =
            db!!.rawQuery(
                "SELECT * FROM task WHERE projectName=? ORDER BY dueDate ASC",
                arrayOf(projectName)
            )

        while(cursor.moveToNext()) {

            list.add(
                TaskModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
            )
        }

        fun updateStatus(id: Int, status: Int) {
            openDatabase()

            val cv = ContentValues()
            cv.put("status", status)

            db!!.update(
                "task",
                cv,
                "id=?",
                arrayOf(id.toString())
            )
        }

        cursor.close()

        return list
    }
    fun updateStatus(id: Int, status: Int) {
        openDatabase()

        val cv = ContentValues()
        cv.put("status", status)

        db!!.update(
            "task",
            cv,
            "id=?",
            arrayOf(id.toString())
        )
    }
    fun deleteTask(id: Int) {
        openDatabase()

        db!!.delete(
            "task",
            "id=?",
            arrayOf(id.toString())
        )
    }
}