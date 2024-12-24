package com.example.studybuddy.data.repository

import com.example.studybuddy.data.local.dao.HomeworkDao
import com.example.studybuddy.domain.model.Homework
import kotlinx.coroutines.flow.Flow

class HomeworkRepository(private val homeworkDao: HomeworkDao) {
    fun insert(homework: Homework) = homeworkDao.insert(homework)
    fun delete(homework: Homework) = homeworkDao.delete(homework)
    fun update(homework: Homework) = homeworkDao.update(homework)

    fun getHomeworkById(homeworkId: Long): Flow<Homework?> = homeworkDao.getHomeworkById(homeworkId)

    val allHomeworks: Flow<List<Homework>> = homeworkDao.getAllHomework()
    fun getTop3Homework(): Flow<List<Homework>> = homeworkDao.getTop3Homework()
}