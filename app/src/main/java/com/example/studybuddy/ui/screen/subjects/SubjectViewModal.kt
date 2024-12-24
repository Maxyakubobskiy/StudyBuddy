package com.example.studybuddy.ui.screen.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.MyApp
import com.example.studybuddy.domain.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SubjectViewModal: ViewModel() {

    private val repository = MyApp.instance.subjectRepository

    val subjectList: Flow<List<Subject>> = repository.allSubjects

    fun addSubject(subject: Subject){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(subject)
        }
    }

    private var lastDeletedSubject: Subject? = null

    fun deleteSubject(subject: Subject){
        lastDeletedSubject = subject
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(subject)
        }
    }
     fun restoreDeletedSubject(){
         lastDeletedSubject?.let {
             viewModelScope.launch(Dispatchers.IO) {
                 repository.insert(it)
             }
             lastDeletedSubject = null
         }
     }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(subject)
        }
    }
}