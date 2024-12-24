package com.example.studybuddy.data.repository

import com.example.studybuddy.data.local.dao.SubjectDao
import com.example.studybuddy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {

    fun insert(subject: Subject) = subjectDao.insert(subject)

    fun delete(subject: Subject) = subjectDao.delete(subject)

    fun update(subject: Subject) = subjectDao.update(subject)

    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

}
