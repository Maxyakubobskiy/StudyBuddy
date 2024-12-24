package com.example.studybuddy

import android.app.Application
import com.example.studybuddy.data.local.database.StudyBuddyDatabase
import com.example.studybuddy.data.repository.HomeworkRepository
import com.example.studybuddy.data.repository.ScheduleEntryRepository
import com.example.studybuddy.data.repository.SubjectRepository

class MyApp : Application() {
    private val database by lazy {
        StudyBuddyDatabase.getDatabase(this)
    }
    val homeworkRepository by lazy {
        HomeworkRepository(database.homeworkDao())
    }

    val subjectRepository by lazy {
        SubjectRepository(database.subjectDao())
    }

    val scheduleEntryRepository by lazy{
        ScheduleEntryRepository(database.scheduleDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApp
            private set
    }
}