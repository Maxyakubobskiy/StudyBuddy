package com.example.studybuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studybuddy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subject ")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert
    fun insert(subject: Subject)

    @Delete
    fun delete(subject: Subject)

    @Update
    fun update(subject: Subject)
}