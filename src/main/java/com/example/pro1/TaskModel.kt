package com.example.pro1

data class TaskModel(
    var id: Int,
    var projectName: String,
    var task: String,
    var member: String,
    var dueDate: String,
    var status: Int
)