package com.example.studybuddy.ui.screen.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.MyApp
import com.example.studybuddy.domain.model.Homework
import com.example.studybuddy.domain.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeworkViewModal : ViewModel() {
    fun addHomework(homework: Homework) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(homework)
        }
    }
    private var lastDeletedHomework: Homework? = null
    fun deleteHomework(homework: Homework) {
        lastDeletedHomework = homework
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(homework)
        }
    }

    fun updateHomework(homework: Homework) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(homework)
        }
    }

    fun getHomeworkById(homeworkId: Long): Flow<Homework?> {
        return repository.getHomeworkById(homeworkId)
    }

    fun restoreDeletedHomework() {
        lastDeletedHomework?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insert(it)
            }
            lastDeletedHomework = null
        }
    }

    private val repository = MyApp.instance.homeworkRepository

    val homeworkList: Flow<List<Homework>> = repository.allHomeworks
}