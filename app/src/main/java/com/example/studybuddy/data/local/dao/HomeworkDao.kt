package com.example.studybuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.enums.HomeworkStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {

    @Query("SELECT * FROM homework ORDER BY dueDate ASC")
    fun getAllHomework(): Flow<List<Homework>>

    @Insert
    fun insert(homework: Homework)

    @Delete
    fun delete(homework: Homework)

    @Update
    fun update(homework: Homework)

    @Query("SELECT * FROM homework WHERE id = :homeworkId")
    fun getHomeworkById(homeworkId: Long): Flow<Homework?>

    @Query("SELECT * FROM homework WHERE status != 'Completed' ORDER BY dueDate LIMIT 3")
    fun getTop3Homework(): Flow<List<Homework>>
}