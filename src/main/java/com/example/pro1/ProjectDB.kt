package com.example.pro1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProjectDB(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var db: SQLiteDatabase? = null

    companion object {

        private const val DATABASE_NAME = "project.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "project"

        private const val ID = "id"
        private const val NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val query =
            "CREATE TABLE $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$NAME TEXT)"

        db?.execSQL(query)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun openDatabase() {
        db = this.writableDatabase
    }

    fun getAllProjects(): ArrayList<ProjectModel> {

        val projectList = ArrayList<ProjectModel>()

        db = this.readableDatabase

        val cursor: Cursor =
            db!!.rawQuery(
                "SELECT * FROM $TABLE_NAME",
                null
            )

        while (cursor.moveToNext()) {

            val project =
                ProjectModel(
                    cursor.getInt(0),
                    cursor.getString(1)
                )

            projectList.add(project)
        }

        cursor.close()

        return projectList
    }

    fun addProject(project: ProjectModel) {

        openDatabase()

        val cv = ContentValues()

        cv.put(NAME, project.name)

        db!!.insert(
            TABLE_NAME,
            null,
            cv
        )
    }

    fun deleteProject(id: Int) {

        openDatabase()

        db!!.delete(
            TABLE_NAME,
            "id=?",
            arrayOf(id.toString())
        )
    }
}