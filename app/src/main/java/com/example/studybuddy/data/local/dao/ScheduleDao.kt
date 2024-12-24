package com.example.studybuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.domain.model.ScheduleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM scheduleEntry")
    fun getAllSchedule(): Flow<List<ScheduleEntry>>

    @Insert
    fun insert(scheduleEntry: ScheduleEntry)

    @Delete
    fun delete(scheduleEntry: ScheduleEntry)

    @Update
    fun update(scheduleEntry: ScheduleEntry)

    @Query("SELECT * FROM scheduleEntry WHERE LOWER(dayOfWeek) = LOWER(:today)")
    fun getTodaySchedule(today: String): Flow<List<ScheduleEntry>>
}