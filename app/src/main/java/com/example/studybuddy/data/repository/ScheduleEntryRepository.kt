package com.example.studybuddy.data.repository

import com.example.studybuddy.data.local.dao.ScheduleDao
import com.example.studybuddy.domain.model.ScheduleEntry
import kotlinx.coroutines.flow.Flow

class ScheduleEntryRepository(private val scheduleDao: ScheduleDao) {

    fun insert(scheduleEntry: ScheduleEntry) = scheduleDao.insert(scheduleEntry)
    fun delete(scheduleEntry: ScheduleEntry) = scheduleDao.delete(scheduleEntry)
    fun update(scheduleEntry: ScheduleEntry) = scheduleDao.update(scheduleEntry)
    fun getTodaySchedule(today: String): Flow<List<ScheduleEntry>> = scheduleDao.getTodaySchedule(today)

    val allScheduleEntry: Flow<List<ScheduleEntry>> = scheduleDao.getAllSchedule()
}