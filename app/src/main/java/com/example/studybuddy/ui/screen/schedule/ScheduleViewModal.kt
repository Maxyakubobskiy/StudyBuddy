package com.example.studybuddy.ui.screen.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studybuddy.MyApp
import com.example.studybuddy.domain.model.ScheduleEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ScheduleViewModal: ViewModel() {

    fun addScheduleEntry(scheduleEntry: ScheduleEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(scheduleEntry)
        }
    }

    private var lastDeletedSchedule: ScheduleEntry? = null

    fun deleteScheduleEntry(scheduleEntry: ScheduleEntry) {
        lastDeletedSchedule = scheduleEntry
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(scheduleEntry)
        }
    }
    fun restoreDeletedSchedule(){
        lastDeletedSchedule?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insert(it)
            }
            lastDeletedSchedule = null
        }
    }
    fun updateScheduleEntry(scheduleEntry: ScheduleEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(scheduleEntry)
        }
    }

    private val repository = MyApp.instance.scheduleEntryRepository

    val scheduleEntryList: Flow<List<ScheduleEntry>> = repository.allScheduleEntry
}