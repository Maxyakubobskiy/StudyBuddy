package com.example.studybuddy.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studybuddy.data.local.dao.HomeworkDao
import com.example.studybuddy.data.local.dao.ScheduleDao
import com.example.studybuddy.data.local.dao.SubjectDao
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.domain.model.ScheduleEntry
import com.example.studybuddy.domain.model.Subject
import com.example.studybuddy.ui.navigation.Screen

@Database(entities = [Homework::class, Subject::class, ScheduleEntry::class], version = 3,exportSchema = false)
@TypeConverters(DateConverter::class, TimeConverter::class)
abstract class StudyBuddyDatabase : RoomDatabase(){

    abstract fun homeworkDao(): HomeworkDao
    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleDao(): ScheduleDao

    companion object{
        @Volatile
        private var INSTANCE: StudyBuddyDatabase? = null

        fun getDatabase(context: Context): StudyBuddyDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room
                    .databaseBuilder(context, StudyBuddyDatabase::class.java, "study_buddy_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance

            }
        }
    }
}