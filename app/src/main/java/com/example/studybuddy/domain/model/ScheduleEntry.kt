package com.example.studybuddy.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
@Entity(tableName = "scheduleEntry")
data class ScheduleEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val subject: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: String,
    val link: String?
)
