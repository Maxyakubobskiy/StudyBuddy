package com.example.studybuddy.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "homework")
data class Homework(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val subject: String,
    val description: String,
    val dueDate: LocalDateTime,
    val status: String
)
